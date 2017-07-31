/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.web.server;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileStoreServletTest extends RepositoryBaseTest {

    @Mock
    IDiagramProfileService profileService;

    @Spy
    @InjectMocks
    private FileStoreServlet servlet = new FileStoreServlet();

    @Before
    public void setup() {
        super.setup();
        when(profileService.findProfile(any(HttpServletRequest.class),
                                        anyString())).thenReturn(profile);
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testDoPostFindProfile() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        try {
            servlet.doPost(request,
                           mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService,
               times(1)).findProfile(request,
                                     "jbpm");
    }

    @Test
    public void testDoPostProfileAlreadySet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        servlet.profile = profile;
        try {
            servlet.doPost(request,
                           mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService,
               never()).findProfile(any(HttpServletRequest.class),
                                    anyString());
    }

    @Test
    public void testEsapeRetData() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("data",
                   "<hello>some value</hello>");

        servlet.profile = profile;
        try {
            servlet.doPost(new TestHttpServletRequest(params),
                           mock(HttpServletResponse.class));

            assertNotNull(servlet.getRetData());
            assertEquals("&lt;hello&gt;some value&lt;/hello&gt;",
                         servlet.getRetData());
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService,
               never()).findProfile(any(HttpServletRequest.class),
                                    anyString());
    }

    @Test
    public void testEncodedRetData() throws Exception {
        Map<String, String> params = new HashMap<>();
        // full bpmn2 process (base64 encoded)
        params.put("data_encoded",
                   "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPGJwbW4yOmRlZmluaXRpb25zIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIHhtbG5zPSJodHRwOi8vd3d3Lm9tZy5vcmcvYnBtbjIwIiB4bWxuczpicG1uMj0iaHR0cDovL3d3dy5vbWcub3JnL3NwZWMvQlBNTi8yMDEwMDUyNC9NT0RFTCIgeG1sbnM6YnBtbmRpPSJodHRwOi8vd3d3Lm9tZy5vcmcvc3BlYy9CUE1OLzIwMTAwNTI0L0RJIiB4bWxuczpicHNpbT0iaHR0cDovL3d3dy5icHNpbS5vcmcvc2NoZW1hcy8xLjAiIHhtbG5zOmNvbG9yPSJodHRwOi8vd3d3Lm9tZy5vcmcvc3BlYy9CUE1OL25vbi1ub3JtYXRpdmUvY29sb3IiIHhtbG5zOmRjPSJodHRwOi8vd3d3Lm9tZy5vcmcvc3BlYy9ERC8yMDEwMDUyNC9EQyIgeG1sbnM6ZGk9Imh0dHA6Ly93d3cub21nLm9yZy9zcGVjL0RELzIwMTAwNTI0L0RJIiB4bWxuczpkcm9vbHM9Imh0dHA6Ly93d3cuamJvc3Mub3JnL2Ryb29scyIgaWQ9Il82c1BESUZFa0VlZUY3Y0x6ZFo1MjJ3IiB4c2k6c2NoZW1hTG9jYXRpb249Imh0dHA6Ly93d3cub21nLm9yZy9zcGVjL0JQTU4vMjAxMDA1MjQvTU9ERUwgQlBNTjIwLnhzZCBodHRwOi8vd3d3Lmpib3NzLm9yZy9kcm9vbHMgZHJvb2xzLnhzZCBodHRwOi8vd3d3LmJwc2ltLm9yZy9zY2hlbWFzLzEuMCBicHNpbS54c2QiIGV4cG9ydGVyPSJqQlBNIERlc2lnbmVyIiBleHBvcnRlclZlcnNpb249IjYuMi4wIiBleHByZXNzaW9uTGFuZ3VhZ2U9Imh0dHA6Ly93d3cubXZlbC5vcmcvMi4wIiB0YXJnZXROYW1lc3BhY2U9Imh0dHA6Ly93d3cub21nLm9yZy9icG1uMjAiIHR5cGVMYW5ndWFnZT0iaHR0cDovL3d3dy5qYXZhLmNvbS9qYXZhVHlwZXMiPgogIDxicG1uMjppdGVtRGVmaW5pdGlvbiBpZD0iX18yMzQ2MDQwRS04NkJELTQ3OTQtQTdFNi00NzIwNDE0MDU4RkZfU2tpcHBhYmxlSW5wdXRYSXRlbSIgc3RydWN0dXJlUmVmPSJPYmplY3QiLz4KICA8YnBtbjI6cHJvY2VzcyBpZD0iZXZhbHVhdGlvbi52aWV3c291cmNlIiBkcm9vbHM6cGFja2FnZU5hbWU9Im9yZy5qYnBtIiBkcm9vbHM6dmVyc2lvbj0iMS4wIiBuYW1lPSJ2aWV3c291cmNlIiBpc0V4ZWN1dGFibGU9InRydWUiPgogICAgPGJwbW4yOnN0YXJ0RXZlbnQgaWQ9InByb2Nlc3NTdGFydEV2ZW50IiBkcm9vbHM6c2VsZWN0YWJsZT0idHJ1ZSIgY29sb3I6YmFja2dyb3VuZC1jb2xvcj0iIzlhY2QzMiIgY29sb3I6Ym9yZGVyLWNvbG9yPSIjMDAwMDAwIiBjb2xvcjpjb2xvcj0iIzAwMDAwMCIgbmFtZT0iIj4KICAgICAgPGJwbW4yOmV4dGVuc2lvbkVsZW1lbnRzPgogICAgICAgIDxkcm9vbHM6bWV0YURhdGEgbmFtZT0iZWxlbWVudG5hbWUiPgogICAgICAgICAgPGRyb29sczptZXRhVmFsdWU+PCFbQ0RBVEFbXV0+PC9kcm9vbHM6bWV0YVZhbHVlPgogICAgICAgIDwvZHJvb2xzOm1ldGFEYXRhPgogICAgICA8L2JwbW4yOmV4dGVuc2lvbkVsZW1lbnRzPgogICAgICA8YnBtbjI6b3V0Z29pbmc+XzkwQTg0M0NDLTk3NjgtNDRFRS05NjEwLTNBQjg2NEVBNjVBMjwvYnBtbjI6b3V0Z29pbmc+CiAgICA8L2JwbW4yOnN0YXJ0RXZlbnQ+CiAgICA8YnBtbjI6dXNlclRhc2sgaWQ9Il8yMzQ2MDQwRS04NkJELTQ3OTQtQTdFNi00NzIwNDE0MDU4RkYiIGRyb29sczpzZWxlY3RhYmxlPSJ0cnVlIiBkcm9vbHM6c2NyaXB0Rm9ybWF0PSJodHRwOi8vd3d3LmphdmEuY29tL2phdmEiIGNvbG9yOmJhY2tncm91bmQtY29sb3I9IiNmYWZhZDIiIGNvbG9yOmJvcmRlci1jb2xvcj0iIzAwMDAwMCIgY29sb3I6Y29sb3I9IiMwMDAwMDAiIG5hbWU9IiYjMjYwNTM7JiMxMjUyNTsmIzIwMTQwOyYjMzg3Mzg7JiMyMTAzMzsmIzEyNDc1OyYjMTI1MTI7JiMxMjUyNDsgJiMyNDM2OTsmIzI1OTEzOyYjMTI1MDE7JiMxMjUyMDsmIzEyNDczOyYjMjc4NzQ7JiMyNDIyMDsmIzEyMzYzOyYjMTI0MDA7JiMxMjQxMjsiPgogICAgICA8YnBtbjI6ZXh0ZW5zaW9uRWxlbWVudHM+CiAgICAgICAgPGRyb29sczptZXRhRGF0YSBuYW1lPSJlbGVtZW50bmFtZSI+CiAgICAgICAgICA8ZHJvb2xzOm1ldGFWYWx1ZT48IVtDREFUQVvml4Xjg63kuqzpnZLliKnjgrvjg6Djg6wK5byx5pS544OV44Oo44K55rOi5bqc44GL44Gw44G8XV0+PC9kcm9vbHM6bWV0YVZhbHVlPgogICAgICAgIDwvZHJvb2xzOm1ldGFEYXRhPgogICAgICA8L2JwbW4yOmV4dGVuc2lvbkVsZW1lbnRzPgogICAgICA8YnBtbjI6aW5jb21pbmc+XzkwQTg0M0NDLTk3NjgtNDRFRS05NjEwLTNBQjg2NEVBNjVBMjwvYnBtbjI6aW5jb21pbmc+CiAgICAgIDxicG1uMjpvdXRnb2luZz5fNDU2NUEyMzAtM0UyMi00NUM5LUFEMEYtOUEzMTU5RTE1Q0VCPC9icG1uMjpvdXRnb2luZz4KICAgICAgPGJwbW4yOmlvU3BlY2lmaWNhdGlvbiBpZD0iXzZzUERJVkVrRWVlRjdjTHpkWjUyMnciPgogICAgICAgIDxicG1uMjpkYXRhSW5wdXQgaWQ9Il8yMzQ2MDQwRS04NkJELTQ3OTQtQTdFNi00NzIwNDE0MDU4RkZfU2tpcHBhYmxlSW5wdXRYIiBkcm9vbHM6ZHR5cGU9Ik9iamVjdCIgaXRlbVN1YmplY3RSZWY9Il9fMjM0NjA0MEUtODZCRC00Nzk0LUE3RTYtNDcyMDQxNDA1OEZGX1NraXBwYWJsZUlucHV0WEl0ZW0iIG5hbWU9IlNraXBwYWJsZSIvPgogICAgICAgIDxicG1uMjppbnB1dFNldCBpZD0iXzZzUERJbEVrRWVlRjdjTHpkWjUyMnciPgogICAgICAgICAgPGJwbW4yOmRhdGFJbnB1dFJlZnM+XzIzNDYwNDBFLTg2QkQtNDc5NC1BN0U2LTQ3MjA0MTQwNThGRl9Ta2lwcGFibGVJbnB1dFg8L2JwbW4yOmRhdGFJbnB1dFJlZnM+CiAgICAgICAgPC9icG1uMjppbnB1dFNldD4KICAgICAgICA8YnBtbjI6b3V0cHV0U2V0IGlkPSJfNnNQREkxRWtFZWVGN2NMemRaNTIydyIvPgogICAgICA8L2JwbW4yOmlvU3BlY2lmaWNhdGlvbj4KICAgICAgPGJwbW4yOmRhdGFJbnB1dEFzc29jaWF0aW9uIGlkPSJfNnNQREpGRWtFZWVGN2NMemRaNTIydyI+CiAgICAgICAgPGJwbW4yOnRhcmdldFJlZj5fMjM0NjA0MEUtODZCRC00Nzk0LUE3RTYtNDcyMDQxNDA1OEZGX1NraXBwYWJsZUlucHV0WDwvYnBtbjI6dGFyZ2V0UmVmPgogICAgICAgIDxicG1uMjphc3NpZ25tZW50IGlkPSJfNnNQREpWRWtFZWVGN2NMemRaNTIydyI+CiAgICAgICAgICA8YnBtbjI6ZnJvbSB4c2k6dHlwZT0iYnBtbjI6dEZvcm1hbEV4cHJlc3Npb24iIGlkPSJfNnNQREpsRWtFZWVGN2NMemRaNTIydyI+dHJ1ZTwvYnBtbjI6ZnJvbT4KICAgICAgICAgIDxicG1uMjp0byB4c2k6dHlwZT0iYnBtbjI6dEZvcm1hbEV4cHJlc3Npb24iIGlkPSJfNnNQREoxRWtFZWVGN2NMemRaNTIydyI+XzIzNDYwNDBFLTg2QkQtNDc5NC1BN0U2LTQ3MjA0MTQwNThGRl9Ta2lwcGFibGVJbnB1dFg8L2JwbW4yOnRvPgogICAgICAgIDwvYnBtbjI6YXNzaWdubWVudD4KICAgICAgPC9icG1uMjpkYXRhSW5wdXRBc3NvY2lhdGlvbj4KICAgIDwvYnBtbjI6dXNlclRhc2s+CiAgICA8YnBtbjI6c2VxdWVuY2VGbG93IGlkPSJfOTBBODQzQ0MtOTc2OC00NEVFLTk2MTAtM0FCODY0RUE2NUEyIiBkcm9vbHM6c2VsZWN0YWJsZT0idHJ1ZSIgY29sb3I6YmFja2dyb3VuZC1jb2xvcj0iIzAwMDAwMCIgY29sb3I6Ym9yZGVyLWNvbG9yPSIjMDAwMDAwIiBjb2xvcjpjb2xvcj0iIzAwMDAwMCIgc291cmNlUmVmPSJwcm9jZXNzU3RhcnRFdmVudCIgdGFyZ2V0UmVmPSJfMjM0NjA0MEUtODZCRC00Nzk0LUE3RTYtNDcyMDQxNDA1OEZGIi8+CiAgICA8YnBtbjI6ZW5kRXZlbnQgaWQ9Il9ERURCMUM2NC04MzVDLTRERUMtODE4QS04QzBEOEI5NUZEQkMiIGRyb29sczpzZWxlY3RhYmxlPSJ0cnVlIiBjb2xvcjpiYWNrZ3JvdW5kLWNvbG9yPSIjZmY2MzQ3IiBjb2xvcjpib3JkZXItY29sb3I9IiMwMDAwMDAiIGNvbG9yOmNvbG9yPSIjMDAwMDAwIiBuYW1lPSIiPgogICAgICA8YnBtbjI6ZXh0ZW5zaW9uRWxlbWVudHM+CiAgICAgICAgPGRyb29sczptZXRhRGF0YSBuYW1lPSJlbGVtZW50bmFtZSI+CiAgICAgICAgICA8ZHJvb2xzOm1ldGFWYWx1ZT48IVtDREFUQVtdXT48L2Ryb29sczptZXRhVmFsdWU+CiAgICAgICAgPC9kcm9vbHM6bWV0YURhdGE+CiAgICAgIDwvYnBtbjI6ZXh0ZW5zaW9uRWxlbWVudHM+CiAgICAgIDxicG1uMjppbmNvbWluZz5fNDU2NUEyMzAtM0UyMi00NUM5LUFEMEYtOUEzMTU5RTE1Q0VCPC9icG1uMjppbmNvbWluZz4KICAgIDwvYnBtbjI6ZW5kRXZlbnQ+CiAgICA8YnBtbjI6c2VxdWVuY2VGbG93IGlkPSJfNDU2NUEyMzAtM0UyMi00NUM5LUFEMEYtOUEzMTU5RTE1Q0VCIiBkcm9vbHM6c2VsZWN0YWJsZT0idHJ1ZSIgY29sb3I6YmFja2dyb3VuZC1jb2xvcj0iIzAwMDAwMCIgY29sb3I6Ym9yZGVyLWNvbG9yPSIjMDAwMDAwIiBjb2xvcjpjb2xvcj0iIzAwMDAwMCIgc291cmNlUmVmPSJfMjM0NjA0MEUtODZCRC00Nzk0LUE3RTYtNDcyMDQxNDA1OEZGIiB0YXJnZXRSZWY9Il9ERURCMUM2NC04MzVDLTRERUMtODE4QS04QzBEOEI5NUZEQkMiLz4KICA8L2JwbW4yOnByb2Nlc3M+CiAgPGJwbW5kaTpCUE1ORGlhZ3JhbSBpZD0iXzZzUERLRkVrRWVlRjdjTHpkWjUyMnciPgogICAgPGJwbW5kaTpCUE1OUGxhbmUgaWQ9Il82c1BES1ZFa0VlZUY3Y0x6ZFo1MjJ3IiBicG1uRWxlbWVudD0iZXZhbHVhdGlvbi52aWV3c291cmNlIj4KICAgICAgPGJwbW5kaTpCUE1OU2hhcGUgaWQ9Il82c1BES2xFa0VlZUY3Y0x6ZFo1MjJ3IiBicG1uRWxlbWVudD0icHJvY2Vzc1N0YXJ0RXZlbnQiPgogICAgICAgIDxkYzpCb3VuZHMgaGVpZ2h0PSIzMC4wIiB3aWR0aD0iMzAuMCIgeD0iMTIwLjAiIHk9IjE2NS4wIi8+CiAgICAgIDwvYnBtbmRpOkJQTU5TaGFwZT4KICAgICAgPGJwbW5kaTpCUE1OU2hhcGUgaWQ9Il82c1BESzFFa0VlZUY3Y0x6ZFo1MjJ3IiBicG1uRWxlbWVudD0iXzIzNDYwNDBFLTg2QkQtNDc5NC1BN0U2LTQ3MjA0MTQwNThGRiI+CiAgICAgICAgPGRjOkJvdW5kcyBoZWlnaHQ9IjE0Ni4wIiB3aWR0aD0iMjAxLjAiIHg9IjIyNS4wIiB5PSI3NS4wIi8+CiAgICAgIDwvYnBtbmRpOkJQTU5TaGFwZT4KICAgICAgPGJwbW5kaTpCUE1OU2hhcGUgaWQ9Il82c1BETEZFa0VlZUY3Y0x6ZFo1MjJ3IiBicG1uRWxlbWVudD0iX0RFREIxQzY0LTgzNUMtNERFQy04MThBLThDMEQ4Qjk1RkRCQyI+CiAgICAgICAgPGRjOkJvdW5kcyBoZWlnaHQ9IjI4LjAiIHdpZHRoPSIyOC4wIiB4PSI1NzAuMCIgeT0iMjcwLjAiLz4KICAgICAgPC9icG1uZGk6QlBNTlNoYXBlPgogICAgICA8YnBtbmRpOkJQTU5FZGdlIGlkPSJfNnNQRExWRWtFZWVGN2NMemRaNTIydyIgYnBtbkVsZW1lbnQ9Il85MEE4NDNDQy05NzY4LTQ0RUUtOTYxMC0zQUI4NjRFQTY1QTIiIHNvdXJjZUVsZW1lbnQ9Il82c1BES2xFa0VlZUY3Y0x6ZFo1MjJ3IiB0YXJnZXRFbGVtZW50PSJfNnNQREsxRWtFZWVGN2NMemRaNTIydyI+CiAgICAgICAgPGRpOndheXBvaW50IHhzaTp0eXBlPSJkYzpQb2ludCIgeD0iMTM1LjAiIHk9IjE4MC4wIi8+CiAgICAgICAgPGRpOndheXBvaW50IHhzaTp0eXBlPSJkYzpQb2ludCIgeD0iMzI1LjUiIHk9IjE0OC4wIi8+CiAgICAgIDwvYnBtbmRpOkJQTU5FZGdlPgogICAgICA8YnBtbmRpOkJQTU5FZGdlIGlkPSJfNnNQRExsRWtFZWVGN2NMemRaNTIydyIgYnBtbkVsZW1lbnQ9Il80NTY1QTIzMC0zRTIyLTQ1QzktQUQwRi05QTMxNTlFMTVDRUIiIHNvdXJjZUVsZW1lbnQ9Il82c1BESzFFa0VlZUY3Y0x6ZFo1MjJ3IiB0YXJnZXRFbGVtZW50PSJfNnNQRExGRWtFZWVGN2NMemRaNTIydyI+CiAgICAgICAgPGRpOndheXBvaW50IHhzaTp0eXBlPSJkYzpQb2ludCIgeD0iMzI1LjUiIHk9IjE0OC4wIi8+CiAgICAgICAgPGRpOndheXBvaW50IHhzaTp0eXBlPSJkYzpQb2ludCIgeD0iNTg0LjAiIHk9IjI4NC4wIi8+CiAgICAgIDwvYnBtbmRpOkJQTU5FZGdlPgogICAgPC9icG1uZGk6QlBNTlBsYW5lPgogIDwvYnBtbmRpOkJQTU5EaWFncmFtPgogIDxicG1uMjpyZWxhdGlvbnNoaXAgaWQ9Il82c1BETDFFa0VlZUY3Y0x6ZFo1MjJ3IiB0eXBlPSJCUFNpbURhdGEiPgogICAgPGJwbW4yOmV4dGVuc2lvbkVsZW1lbnRzPgogICAgICA8YnBzaW06QlBTaW1EYXRhPgogICAgICAgIDxicHNpbTpTY2VuYXJpbyB4c2k6dHlwZT0iYnBzaW06U2NlbmFyaW8iIGlkPSJkZWZhdWx0IiBuYW1lPSJTaW11bGF0aW9uc2NlbmFyaW8iPgogICAgICAgICAgPGJwc2ltOlNjZW5hcmlvUGFyYW1ldGVycyB4c2k6dHlwZT0iYnBzaW06U2NlbmFyaW9QYXJhbWV0ZXJzIiBiYXNlVGltZVVuaXQ9Im1pbiIvPgogICAgICAgICAgPGJwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzIHhzaTp0eXBlPSJicHNpbTpFbGVtZW50UGFyYW1ldGVycyIgZWxlbWVudFJlZj0iXzkwQTg0M0NDLTk3NjgtNDRFRS05NjEwLTNBQjg2NEVBNjVBMiIgaWQ9Il82c1BETUZFa0VlZUY3Y0x6ZFo1MjJ3Ij4KICAgICAgICAgICAgPGJwc2ltOkNvbnRyb2xQYXJhbWV0ZXJzIHhzaTp0eXBlPSJicHNpbTpDb250cm9sUGFyYW1ldGVycyI+CiAgICAgICAgICAgICAgPGJwc2ltOlByb2JhYmlsaXR5IHhzaTp0eXBlPSJicHNpbTpQYXJhbWV0ZXIiPgogICAgICAgICAgICAgICAgPGJwc2ltOkZsb2F0aW5nUGFyYW1ldGVyIHZhbHVlPSIxMDAuMCIvPgogICAgICAgICAgICAgIDwvYnBzaW06UHJvYmFiaWxpdHk+CiAgICAgICAgICAgIDwvYnBzaW06Q29udHJvbFBhcmFtZXRlcnM+CiAgICAgICAgICA8L2Jwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzPgogICAgICAgICAgPGJwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzIHhzaTp0eXBlPSJicHNpbTpFbGVtZW50UGFyYW1ldGVycyIgZWxlbWVudFJlZj0iXzQ1NjVBMjMwLTNFMjItNDVDOS1BRDBGLTlBMzE1OUUxNUNFQiIgaWQ9Il82c1BETVZFa0VlZUY3Y0x6ZFo1MjJ3Ij4KICAgICAgICAgICAgPGJwc2ltOkNvbnRyb2xQYXJhbWV0ZXJzIHhzaTp0eXBlPSJicHNpbTpDb250cm9sUGFyYW1ldGVycyI+CiAgICAgICAgICAgICAgPGJwc2ltOlByb2JhYmlsaXR5IHhzaTp0eXBlPSJicHNpbTpQYXJhbWV0ZXIiPgogICAgICAgICAgICAgICAgPGJwc2ltOkZsb2F0aW5nUGFyYW1ldGVyIHZhbHVlPSIxMDAuMCIvPgogICAgICAgICAgICAgIDwvYnBzaW06UHJvYmFiaWxpdHk+CiAgICAgICAgICAgIDwvYnBzaW06Q29udHJvbFBhcmFtZXRlcnM+CiAgICAgICAgICA8L2Jwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzPgogICAgICAgICAgPGJwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzIHhzaTp0eXBlPSJicHNpbTpFbGVtZW50UGFyYW1ldGVycyIgZWxlbWVudFJlZj0icHJvY2Vzc1N0YXJ0RXZlbnQiIGlkPSJfNnNQRE1sRWtFZWVGN2NMemRaNTIydyI+CiAgICAgICAgICAgIDxicHNpbTpUaW1lUGFyYW1ldGVycyB4c2k6dHlwZT0iYnBzaW06VGltZVBhcmFtZXRlcnMiPgogICAgICAgICAgICAgIDxicHNpbTpQcm9jZXNzaW5nVGltZSB4c2k6dHlwZT0iYnBzaW06UGFyYW1ldGVyIj4KICAgICAgICAgICAgICAgIDxicHNpbTpVbmlmb3JtRGlzdHJpYnV0aW9uIG1heD0iMTAuMCIgbWluPSI1LjAiLz4KICAgICAgICAgICAgICA8L2Jwc2ltOlByb2Nlc3NpbmdUaW1lPgogICAgICAgICAgICA8L2Jwc2ltOlRpbWVQYXJhbWV0ZXJzPgogICAgICAgICAgICA8YnBzaW06Q29udHJvbFBhcmFtZXRlcnMgeHNpOnR5cGU9ImJwc2ltOkNvbnRyb2xQYXJhbWV0ZXJzIj4KICAgICAgICAgICAgICA8YnBzaW06UHJvYmFiaWxpdHkgeHNpOnR5cGU9ImJwc2ltOlBhcmFtZXRlciI+CiAgICAgICAgICAgICAgICA8YnBzaW06RmxvYXRpbmdQYXJhbWV0ZXIgdmFsdWU9IjEwMC4wIi8+CiAgICAgICAgICAgICAgPC9icHNpbTpQcm9iYWJpbGl0eT4KICAgICAgICAgICAgPC9icHNpbTpDb250cm9sUGFyYW1ldGVycz4KICAgICAgICAgIDwvYnBzaW06RWxlbWVudFBhcmFtZXRlcnM+CiAgICAgICAgICA8YnBzaW06RWxlbWVudFBhcmFtZXRlcnMgeHNpOnR5cGU9ImJwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzIiBlbGVtZW50UmVmPSJfMjM0NjA0MEUtODZCRC00Nzk0LUE3RTYtNDcyMDQxNDA1OEZGIiBpZD0iXzZzUERNMUVrRWVlRjdjTHpkWjUyMnciPgogICAgICAgICAgICA8YnBzaW06VGltZVBhcmFtZXRlcnMgeHNpOnR5cGU9ImJwc2ltOlRpbWVQYXJhbWV0ZXJzIj4KICAgICAgICAgICAgICA8YnBzaW06UHJvY2Vzc2luZ1RpbWUgeHNpOnR5cGU9ImJwc2ltOlBhcmFtZXRlciI+CiAgICAgICAgICAgICAgICA8YnBzaW06VW5pZm9ybURpc3RyaWJ1dGlvbiBtYXg9IjEwLjAiIG1pbj0iNS4wIi8+CiAgICAgICAgICAgICAgPC9icHNpbTpQcm9jZXNzaW5nVGltZT4KICAgICAgICAgICAgPC9icHNpbTpUaW1lUGFyYW1ldGVycz4KICAgICAgICAgICAgPGJwc2ltOlJlc291cmNlUGFyYW1ldGVycyB4c2k6dHlwZT0iYnBzaW06UmVzb3VyY2VQYXJhbWV0ZXJzIj4KICAgICAgICAgICAgICA8YnBzaW06QXZhaWxhYmlsaXR5IHhzaTp0eXBlPSJicHNpbTpQYXJhbWV0ZXIiPgogICAgICAgICAgICAgICAgPGJwc2ltOkZsb2F0aW5nUGFyYW1ldGVyIHZhbHVlPSI4LjAiLz4KICAgICAgICAgICAgICA8L2Jwc2ltOkF2YWlsYWJpbGl0eT4KICAgICAgICAgICAgICA8YnBzaW06UXVhbnRpdHkgeHNpOnR5cGU9ImJwc2ltOlBhcmFtZXRlciI+CiAgICAgICAgICAgICAgICA8YnBzaW06RmxvYXRpbmdQYXJhbWV0ZXIgdmFsdWU9IjEuMCIvPgogICAgICAgICAgICAgIDwvYnBzaW06UXVhbnRpdHk+CiAgICAgICAgICAgIDwvYnBzaW06UmVzb3VyY2VQYXJhbWV0ZXJzPgogICAgICAgICAgICA8YnBzaW06Q29zdFBhcmFtZXRlcnMgeHNpOnR5cGU9ImJwc2ltOkNvc3RQYXJhbWV0ZXJzIj4KICAgICAgICAgICAgICA8YnBzaW06VW5pdENvc3QgeHNpOnR5cGU9ImJwc2ltOlBhcmFtZXRlciI+CiAgICAgICAgICAgICAgICA8YnBzaW06RmxvYXRpbmdQYXJhbWV0ZXIgdmFsdWU9IjAuMCIvPgogICAgICAgICAgICAgIDwvYnBzaW06VW5pdENvc3Q+CiAgICAgICAgICAgIDwvYnBzaW06Q29zdFBhcmFtZXRlcnM+CiAgICAgICAgICA8L2Jwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzPgogICAgICAgICAgPGJwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzIHhzaTp0eXBlPSJicHNpbTpFbGVtZW50UGFyYW1ldGVycyIgZWxlbWVudFJlZj0iX0RFREIxQzY0LTgzNUMtNERFQy04MThBLThDMEQ4Qjk1RkRCQyIgaWQ9Il82c1BETkZFa0VlZUY3Y0x6ZFo1MjJ3Ij4KICAgICAgICAgICAgPGJwc2ltOlRpbWVQYXJhbWV0ZXJzIHhzaTp0eXBlPSJicHNpbTpUaW1lUGFyYW1ldGVycyI+CiAgICAgICAgICAgICAgPGJwc2ltOlByb2Nlc3NpbmdUaW1lIHhzaTp0eXBlPSJicHNpbTpQYXJhbWV0ZXIiPgogICAgICAgICAgICAgICAgPGJwc2ltOlVuaWZvcm1EaXN0cmlidXRpb24gbWF4PSIxMC4wIiBtaW49IjUuMCIvPgogICAgICAgICAgICAgIDwvYnBzaW06UHJvY2Vzc2luZ1RpbWU+CiAgICAgICAgICAgIDwvYnBzaW06VGltZVBhcmFtZXRlcnM+CiAgICAgICAgICA8L2Jwc2ltOkVsZW1lbnRQYXJhbWV0ZXJzPgogICAgICAgIDwvYnBzaW06U2NlbmFyaW8+CiAgICAgIDwvYnBzaW06QlBTaW1EYXRhPgogICAgPC9icG1uMjpleHRlbnNpb25FbGVtZW50cz4KICAgIDxicG1uMjpzb3VyY2U+XzZzUERJRkVrRWVlRjdjTHpkWjUyMnc8L2JwbW4yOnNvdXJjZT4KICAgIDxicG1uMjp0YXJnZXQ+XzZzUERJRkVrRWVlRjdjTHpkWjUyMnc8L2JwbW4yOnRhcmdldD4KICA8L2JwbW4yOnJlbGF0aW9uc2hpcD4KPC9icG1uMjpkZWZpbml0aW9ucz4K");

        servlet.profile = profile;

        servlet.doPost(new TestHttpServletRequest(params),
                       mock(HttpServletResponse.class));

        assertNotNull(servlet.getRetData());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                             "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:color=\"http://www.omg.org/spec/BPMN/non-normative/color\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:drools=\"http://www.jboss.org/drools\" id=\"_6sPDIFEkEeeF7cLzdZ522w\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd\" exporter=\"jBPM Designer\" exporterVersion=\"6.2.0\" expressionLanguage=\"http://www.mvel.org/2.0\" targetNamespace=\"http://www.omg.org/bpmn20\" typeLanguage=\"http://www.java.com/javaTypes\">\n" +
                             "  <bpmn2:itemDefinition id=\"__2346040E-86BD-4794-A7E6-4720414058FF_SkippableInputXItem\" structureRef=\"Object\"/>\n" +
                             "  <bpmn2:process id=\"evaluation.viewsource\" drools:packageName=\"org.jbpm\" drools:version=\"1.0\" name=\"viewsource\" isExecutable=\"true\">\n" +
                             "    <bpmn2:startEvent id=\"processStartEvent\" drools:selectable=\"true\" color:background-color=\"#9acd32\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"\">\n" +
                             "      <bpmn2:extensionElements>\n" +
                             "        <drools:metaData name=\"elementname\">\n" +
                             "          <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                             "        </drools:metaData>\n" +
                             "      </bpmn2:extensionElements>\n" +
                             "      <bpmn2:outgoing>_90A843CC-9768-44EE-9610-3AB864EA65A2</bpmn2:outgoing>\n" +
                             "    </bpmn2:startEvent>\n" +
                             "    <bpmn2:userTask id=\"_2346040E-86BD-4794-A7E6-4720414058FF\" drools:selectable=\"true\" drools:scriptFormat=\"http://www.java.com/java\" color:background-color=\"#fafad2\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"&#26053;&#12525;&#20140;&#38738;&#21033;&#12475;&#12512;&#12524; &#24369;&#25913;&#12501;&#12520;&#12473;&#27874;&#24220;&#12363;&#12400;&#12412;\">\n" +
                             "      <bpmn2:extensionElements>\n" +
                             "        <drools:metaData name=\"elementname\">\n" +
                             "          <drools:metaValue><![CDATA[旅ロ京青利セムレ\n" +
                             "弱改フヨス波府かばぼ]]></drools:metaValue>\n" +
                             "        </drools:metaData>\n" +
                             "      </bpmn2:extensionElements>\n" +
                             "      <bpmn2:incoming>_90A843CC-9768-44EE-9610-3AB864EA65A2</bpmn2:incoming>\n" +
                             "      <bpmn2:outgoing>_4565A230-3E22-45C9-AD0F-9A3159E15CEB</bpmn2:outgoing>\n" +
                             "      <bpmn2:ioSpecification id=\"_6sPDIVEkEeeF7cLzdZ522w\">\n" +
                             "        <bpmn2:dataInput id=\"_2346040E-86BD-4794-A7E6-4720414058FF_SkippableInputX\" drools:dtype=\"Object\" itemSubjectRef=\"__2346040E-86BD-4794-A7E6-4720414058FF_SkippableInputXItem\" name=\"Skippable\"/>\n" +
                             "        <bpmn2:inputSet id=\"_6sPDIlEkEeeF7cLzdZ522w\">\n" +
                             "          <bpmn2:dataInputRefs>_2346040E-86BD-4794-A7E6-4720414058FF_SkippableInputX</bpmn2:dataInputRefs>\n" +
                             "        </bpmn2:inputSet>\n" +
                             "        <bpmn2:outputSet id=\"_6sPDI1EkEeeF7cLzdZ522w\"/>\n" +
                             "      </bpmn2:ioSpecification>\n" +
                             "      <bpmn2:dataInputAssociation id=\"_6sPDJFEkEeeF7cLzdZ522w\">\n" +
                             "        <bpmn2:targetRef>_2346040E-86BD-4794-A7E6-4720414058FF_SkippableInputX</bpmn2:targetRef>\n" +
                             "        <bpmn2:assignment id=\"_6sPDJVEkEeeF7cLzdZ522w\">\n" +
                             "          <bpmn2:from xsi:type=\"bpmn2:tFormalExpression\" id=\"_6sPDJlEkEeeF7cLzdZ522w\">true</bpmn2:from>\n" +
                             "          <bpmn2:to xsi:type=\"bpmn2:tFormalExpression\" id=\"_6sPDJ1EkEeeF7cLzdZ522w\">_2346040E-86BD-4794-A7E6-4720414058FF_SkippableInputX</bpmn2:to>\n" +
                             "        </bpmn2:assignment>\n" +
                             "      </bpmn2:dataInputAssociation>\n" +
                             "    </bpmn2:userTask>\n" +
                             "    <bpmn2:sequenceFlow id=\"_90A843CC-9768-44EE-9610-3AB864EA65A2\" drools:selectable=\"true\" color:background-color=\"#000000\" color:border-color=\"#000000\" color:color=\"#000000\" sourceRef=\"processStartEvent\" targetRef=\"_2346040E-86BD-4794-A7E6-4720414058FF\"/>\n" +
                             "    <bpmn2:endEvent id=\"_DEDB1C64-835C-4DEC-818A-8C0D8B95FDBC\" drools:selectable=\"true\" color:background-color=\"#ff6347\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"\">\n" +
                             "      <bpmn2:extensionElements>\n" +
                             "        <drools:metaData name=\"elementname\">\n" +
                             "          <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                             "        </drools:metaData>\n" +
                             "      </bpmn2:extensionElements>\n" +
                             "      <bpmn2:incoming>_4565A230-3E22-45C9-AD0F-9A3159E15CEB</bpmn2:incoming>\n" +
                             "    </bpmn2:endEvent>\n" +
                             "    <bpmn2:sequenceFlow id=\"_4565A230-3E22-45C9-AD0F-9A3159E15CEB\" drools:selectable=\"true\" color:background-color=\"#000000\" color:border-color=\"#000000\" color:color=\"#000000\" sourceRef=\"_2346040E-86BD-4794-A7E6-4720414058FF\" targetRef=\"_DEDB1C64-835C-4DEC-818A-8C0D8B95FDBC\"/>\n" +
                             "  </bpmn2:process>\n" +
                             "  <bpmndi:BPMNDiagram id=\"_6sPDKFEkEeeF7cLzdZ522w\">\n" +
                             "    <bpmndi:BPMNPlane id=\"_6sPDKVEkEeeF7cLzdZ522w\" bpmnElement=\"evaluation.viewsource\">\n" +
                             "      <bpmndi:BPMNShape id=\"_6sPDKlEkEeeF7cLzdZ522w\" bpmnElement=\"processStartEvent\">\n" +
                             "        <dc:Bounds height=\"30.0\" width=\"30.0\" x=\"120.0\" y=\"165.0\"/>\n" +
                             "      </bpmndi:BPMNShape>\n" +
                             "      <bpmndi:BPMNShape id=\"_6sPDK1EkEeeF7cLzdZ522w\" bpmnElement=\"_2346040E-86BD-4794-A7E6-4720414058FF\">\n" +
                             "        <dc:Bounds height=\"146.0\" width=\"201.0\" x=\"225.0\" y=\"75.0\"/>\n" +
                             "      </bpmndi:BPMNShape>\n" +
                             "      <bpmndi:BPMNShape id=\"_6sPDLFEkEeeF7cLzdZ522w\" bpmnElement=\"_DEDB1C64-835C-4DEC-818A-8C0D8B95FDBC\">\n" +
                             "        <dc:Bounds height=\"28.0\" width=\"28.0\" x=\"570.0\" y=\"270.0\"/>\n" +
                             "      </bpmndi:BPMNShape>\n" +
                             "      <bpmndi:BPMNEdge id=\"_6sPDLVEkEeeF7cLzdZ522w\" bpmnElement=\"_90A843CC-9768-44EE-9610-3AB864EA65A2\" sourceElement=\"_6sPDKlEkEeeF7cLzdZ522w\" targetElement=\"_6sPDK1EkEeeF7cLzdZ522w\">\n" +
                             "        <di:waypoint xsi:type=\"dc:Point\" x=\"135.0\" y=\"180.0\"/>\n" +
                             "        <di:waypoint xsi:type=\"dc:Point\" x=\"325.5\" y=\"148.0\"/>\n" +
                             "      </bpmndi:BPMNEdge>\n" +
                             "      <bpmndi:BPMNEdge id=\"_6sPDLlEkEeeF7cLzdZ522w\" bpmnElement=\"_4565A230-3E22-45C9-AD0F-9A3159E15CEB\" sourceElement=\"_6sPDK1EkEeeF7cLzdZ522w\" targetElement=\"_6sPDLFEkEeeF7cLzdZ522w\">\n" +
                             "        <di:waypoint xsi:type=\"dc:Point\" x=\"325.5\" y=\"148.0\"/>\n" +
                             "        <di:waypoint xsi:type=\"dc:Point\" x=\"584.0\" y=\"284.0\"/>\n" +
                             "      </bpmndi:BPMNEdge>\n" +
                             "    </bpmndi:BPMNPlane>\n" +
                             "  </bpmndi:BPMNDiagram>\n" +
                             "  <bpmn2:relationship id=\"_6sPDL1EkEeeF7cLzdZ522w\" type=\"BPSimData\">\n" +
                             "    <bpmn2:extensionElements>\n" +
                             "      <bpsim:BPSimData>\n" +
                             "        <bpsim:Scenario xsi:type=\"bpsim:Scenario\" id=\"default\" name=\"Simulationscenario\">\n" +
                             "          <bpsim:ScenarioParameters xsi:type=\"bpsim:ScenarioParameters\" baseTimeUnit=\"min\"/>\n" +
                             "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_90A843CC-9768-44EE-9610-3AB864EA65A2\" id=\"_6sPDMFEkEeeF7cLzdZ522w\">\n" +
                             "            <bpsim:ControlParameters xsi:type=\"bpsim:ControlParameters\">\n" +
                             "              <bpsim:Probability xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:FloatingParameter value=\"100.0\"/>\n" +
                             "              </bpsim:Probability>\n" +
                             "            </bpsim:ControlParameters>\n" +
                             "          </bpsim:ElementParameters>\n" +
                             "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_4565A230-3E22-45C9-AD0F-9A3159E15CEB\" id=\"_6sPDMVEkEeeF7cLzdZ522w\">\n" +
                             "            <bpsim:ControlParameters xsi:type=\"bpsim:ControlParameters\">\n" +
                             "              <bpsim:Probability xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:FloatingParameter value=\"100.0\"/>\n" +
                             "              </bpsim:Probability>\n" +
                             "            </bpsim:ControlParameters>\n" +
                             "          </bpsim:ElementParameters>\n" +
                             "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"processStartEvent\" id=\"_6sPDMlEkEeeF7cLzdZ522w\">\n" +
                             "            <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">\n" +
                             "              <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:UniformDistribution max=\"10.0\" min=\"5.0\"/>\n" +
                             "              </bpsim:ProcessingTime>\n" +
                             "            </bpsim:TimeParameters>\n" +
                             "            <bpsim:ControlParameters xsi:type=\"bpsim:ControlParameters\">\n" +
                             "              <bpsim:Probability xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:FloatingParameter value=\"100.0\"/>\n" +
                             "              </bpsim:Probability>\n" +
                             "            </bpsim:ControlParameters>\n" +
                             "          </bpsim:ElementParameters>\n" +
                             "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_2346040E-86BD-4794-A7E6-4720414058FF\" id=\"_6sPDM1EkEeeF7cLzdZ522w\">\n" +
                             "            <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">\n" +
                             "              <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:UniformDistribution max=\"10.0\" min=\"5.0\"/>\n" +
                             "              </bpsim:ProcessingTime>\n" +
                             "            </bpsim:TimeParameters>\n" +
                             "            <bpsim:ResourceParameters xsi:type=\"bpsim:ResourceParameters\">\n" +
                             "              <bpsim:Availability xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:FloatingParameter value=\"8.0\"/>\n" +
                             "              </bpsim:Availability>\n" +
                             "              <bpsim:Quantity xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:FloatingParameter value=\"1.0\"/>\n" +
                             "              </bpsim:Quantity>\n" +
                             "            </bpsim:ResourceParameters>\n" +
                             "            <bpsim:CostParameters xsi:type=\"bpsim:CostParameters\">\n" +
                             "              <bpsim:UnitCost xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:FloatingParameter value=\"0.0\"/>\n" +
                             "              </bpsim:UnitCost>\n" +
                             "            </bpsim:CostParameters>\n" +
                             "          </bpsim:ElementParameters>\n" +
                             "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_DEDB1C64-835C-4DEC-818A-8C0D8B95FDBC\" id=\"_6sPDNFEkEeeF7cLzdZ522w\">\n" +
                             "            <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">\n" +
                             "              <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">\n" +
                             "                <bpsim:UniformDistribution max=\"10.0\" min=\"5.0\"/>\n" +
                             "              </bpsim:ProcessingTime>\n" +
                             "            </bpsim:TimeParameters>\n" +
                             "          </bpsim:ElementParameters>\n" +
                             "        </bpsim:Scenario>\n" +
                             "      </bpsim:BPSimData>\n" +
                             "    </bpmn2:extensionElements>\n" +
                             "    <bpmn2:source>_6sPDIFEkEeeF7cLzdZ522w</bpmn2:source>\n" +
                             "    <bpmn2:target>_6sPDIFEkEeeF7cLzdZ522w</bpmn2:target>\n" +
                             "  </bpmn2:relationship>\n" +
                             "</bpmn2:definitions>\n",
                     servlet.getRetData());
    }
}
