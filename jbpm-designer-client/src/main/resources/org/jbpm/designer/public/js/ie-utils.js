/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// BZ1130465 - Designer doesn't fully load in IE9 due to missing function Range.createContextualFragment
if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment)
{
    Range.prototype.createContextualFragment = function(html)
    {
        var frag = document.createDocumentFragment(),
                div = document.createElement("div");
        frag.appendChild(div);
        div.outerHTML = html;
        return frag;
    };
}

// BZ1130465 - Designer cannot save BP's in IE9 due to missing function window.btoa
if (!window.btoa) {
    window.btoa = function(input) { return Base64.encode(input); };
}
if (!window.atob) {
    window.atob = function(input) { return Base64.decode(input); };
}
