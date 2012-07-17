import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;

[
  [
    "name" : "Email",
    "parameters" : [
      "From" : new StringDataType(),
      "To" : new StringDataType(),
      "Subject" : new StringDataType(),
      "Body" : new StringDataType()
    ],
    "displayName" : "Email",
    "icon" : "$protocol$://$host$/$subdomain$/rest/packages/$pkgName$/assets/defaultemailicon/binary"
  ],
  
  [
    "name" : "Log",
    "parameters" : [
      "Message" : new StringDataType()
    ],
    "displayName" : "Log",
    "icon" : "$protocol$://$host$/$subdomain$/rest/packages/$pkgName$/assets/defaultlogicon/binary"
  ],
  
  [
    "name" : "MicrosoftAcademy",
    "parameters" : [
      "title" : new StringDataType()
    ], 
 	"results" : [ 
    	"results" : new ObjectDataType("java.util.Map") 
  	], 
    "displayName" : "Microsoft Academy",
    "icon" : "$protocol$://$host$/$subdomain$/rest/packages/$pkgName$/assets/micadicon/binary"
  ]
]
