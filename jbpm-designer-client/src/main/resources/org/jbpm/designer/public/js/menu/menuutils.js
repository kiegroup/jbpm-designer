function openintab(url) {
    var togo = window.location.protocol + "//" + window.location.host + "/designer/editor?uuid=" + url + "&profile=jbpm&pp=";
    window.open(togo, '_blank');
}