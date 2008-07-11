package org.oryxeditor.server.auth;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegRequest;

import org.openid4java.util.ProxyProperties;
import org.openid4java.util.HttpClientFactory;

/**
* This servlet provedes openID authentication.
*
* This servlet is based on the work of Sutra Zhou, who published it within the
* openid4java project under the Apache License, version 2.0.
*
* @author Martin Czuchra, Sutra Zhou
*/
public class OpenIDAuthenticationServlet extends HttpServlet {

   private static final long serialVersionUID = -6968062295268437353L;
   private ConsumerManager manager;

   public static final String OPENID_SESSION_IDENTIFIER = "openid";
   public static final String REPOSITORY_REDIRECT = "/poem/repository";

   public void init(ServletConfig config) throws ServletException {
       super.init(config);

       config.getServletContext();

       try {
           // --- Forward proxy setup (only if needed) --- 
           String proxyHostName = getInitParameter("proxy-host-name").trim();
           String proxyPort = getInitParameter("proxy-port").trim();
           
           if (proxyHostName.length() > 0 && proxyPort.length() > 0) {
               ProxyProperties proxyProps = new ProxyProperties(); 
               proxyProps.setProxyHostName(proxyHostName); 
               proxyProps.setProxyPort(Integer.valueOf(proxyPort)); 
               HttpClientFactory.setProxyProperties(proxyProps);
           }

           this.manager = new ConsumerManager();
           manager.setAssociations(new InMemoryConsumerAssociationStore());
           manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
       } catch (ConsumerException e) {
           throw new ServletException(e);
       }
   }

   /*
    * (non-Javadoc)
    *
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
       doPost(req, resp);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
       if ("true".equals(req.getParameter("is_return"))) {
           processReturn(req, resp);
       } else {
    	   // Convert OpenID identifier to lower case in order to prevent creating 	   
    	   // different accounts for the same OpenID
           String identifier = req.getParameter("openid_identifier").toLowerCase();
           this.authRequest(identifier, req, resp);
       }
   }

   private void processReturn(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
       Identifier identifier = this.verifyResponse(req);
       if (identifier == null) {
           this.getServletContext().getRequestDispatcher("/index.jsp")
                   .forward(req, resp);
       } else {

           // authentication successful.

           // store openid in session for future use by ruby dispatcher.
           req.getSession().setAttribute(OPENID_SESSION_IDENTIFIER,
                   identifier.getIdentifier());

           req.setAttribute("identifier", identifier.getIdentifier());
           resp.sendRedirect(req.getContextPath() + REPOSITORY_REDIRECT);
       }
   }

   // --- placing the authentication request ---
   @SuppressWarnings("unchecked")
   public String authRequest(String userSuppliedString,
           HttpServletRequest httpReq, HttpServletResponse httpResp)
           throws IOException, ServletException {
       try {
           // configure the return_to URL where your application will receive
           // the authentication responses from the OpenID provider
           // String returnToUrl = "http://example.com/openid";
           String returnToUrl = httpReq.getRequestURL().toString()
                   + "?is_return=true";

           // perform discovery on the user-supplied identifier
           List discoveries = manager.discover(userSuppliedString);

           // attempt to associate with the OpenID provider
           // and retrieve one service endpoint for authentication
           DiscoveryInformation discovered = manager.associate(discoveries);

           // store the discovery information in the user's session
           httpReq.getSession().setAttribute("openid-disc", discovered);

           // obtain a AuthRequest message to be sent to the OpenID provider
           AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

           // Attribute Exchange example: fetching the 'email' attribute
           FetchRequest fetch = FetchRequest.createFetchRequest();
           SRegRequest sregReq = SRegRequest.createFetchRequest();

           if ("1".equals(httpReq.getParameter("nickname"))) {
               // fetch.addAttribute("nickname",
               // "http://schema.openid.net/contact/nickname", false);
               sregReq.addAttribute("nickname", false);
           }
           if ("1".equals(httpReq.getParameter("email"))) {
               fetch.addAttribute("email",
                       "http://schema.openid.net/contact/email", false);
               sregReq.addAttribute("email", false);
           }
           if ("1".equals(httpReq.getParameter("fullname"))) {
               fetch.addAttribute("fullname",
                       "http://schema.openid.net/contact/fullname", false);
               sregReq.addAttribute("fullname", false);
           }
           if ("1".equals(httpReq.getParameter("dob"))) {
               fetch.addAttribute("dob",
                       "http://schema.openid.net/contact/dob", true);
               sregReq.addAttribute("dob", false);
           }
           if ("1".equals(httpReq.getParameter("gender"))) {
               fetch.addAttribute("gender",
                       "http://schema.openid.net/contact/gender", false);
               sregReq.addAttribute("gender", false);
           }
           if ("1".equals(httpReq.getParameter("postcode"))) {
               fetch.addAttribute("postcode",
                       "http://schema.openid.net/contact/postcode", false);
               sregReq.addAttribute("postcode", false);
           }
           if ("1".equals(httpReq.getParameter("country"))) {
               fetch.addAttribute("country",
                       "http://schema.openid.net/contact/country", false);
               sregReq.addAttribute("country", false);
           }
           if ("1".equals(httpReq.getParameter("language"))) {
               fetch.addAttribute("language",
                       "http://schema.openid.net/contact/language", false);
               sregReq.addAttribute("language", false);
           }
           if ("1".equals(httpReq.getParameter("timezone"))) {
               fetch.addAttribute("timezone",
                       "http://schema.openid.net/contact/timezone", false);
               sregReq.addAttribute("timezone", false);
           }

           // attach the extension to the authentication request
           if (!sregReq.getAttributes().isEmpty()) {
               authReq.addExtension(sregReq);
           }

           if (!discovered.isVersion2()) {
               // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
               // The only method supported in OpenID 1.x
               // redirect-URL usually limited ~2048 bytes
               httpResp.sendRedirect(authReq.getDestinationUrl(true));
               return null;
           } else {
               // Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)

               RequestDispatcher dispatcher = getServletContext()
                       .getRequestDispatcher("/formredirection.jsp");
               httpReq.setAttribute("prameterMap", httpReq.getParameterMap());
               httpReq.setAttribute("message", authReq);
               // httpReq.setAttribute("destinationUrl", httpResp
               // .getDestinationUrl(false));
               dispatcher.forward(httpReq, httpResp);
           }
       } catch (OpenIDException e) {
           // present error to the user
           throw new ServletException(e);
       }


       return null;
   }

   // --- processing the authentication response ---
   @SuppressWarnings("unchecked")
   public Identifier verifyResponse(HttpServletRequest httpReq)
           throws ServletException {
       try {
           // extract the parameters from the authentication response
           // (which comes in as a HTTP request from the OpenID provider)
           ParameterList response = new ParameterList(httpReq
                   .getParameterMap());

           // retrieve the previously stored discovery information
           DiscoveryInformation discovered = (DiscoveryInformation) httpReq
                   .getSession().getAttribute("openid-disc");

           // extract the receiving URL from the HTTP request
           StringBuffer receivingURL = httpReq.getRequestURL();
           String queryString = httpReq.getQueryString();
           if (queryString != null && queryString.length() > 0)
               receivingURL.append("?").append(httpReq.getQueryString());

           // verify the response; ConsumerManager needs to be the same
           // (static) instance used to place the authentication request
           VerificationResult verification = manager.verify(receivingURL
                   .toString(), response, discovered);

           // examine the verification result and extract the verified
           // identifier
           Identifier verified = verification.getVerifiedId();
           if (verified != null) {
               AuthSuccess authSuccess = (AuthSuccess) verification
                       .getAuthResponse();

               if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                   FetchResponse fetchResp = (FetchResponse) authSuccess
                           .getExtension(AxMessage.OPENID_NS_AX);

                   // List emails = fetchResp.getAttributeValues("email");
                   // String email = (String) emails.get(0);

                   List aliases = fetchResp.getAttributeAliases();
                   for (Iterator iter = aliases.iterator(); iter.hasNext();) {
                       String alias = (String) iter.next();
                       List values = fetchResp.getAttributeValues(alias);
                       if (values.size() > 0) {
                           httpReq.setAttribute(alias, values.get(0));
                       }
                   }
               }

               return verified; // success
           }
       } catch (OpenIDException e) {
           // present error to the user
           throw new ServletException(e);
       }

       return null;
   }

}
