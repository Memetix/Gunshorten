<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Gunshorten - Expand your URLs" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
    </head>
    <body>
        <div id="logo"><a href="${request.contextPath}/"><img src="${resource(dir: 'images', file: 'logo.png')}" alt="Gunshorten API"/></a></div>
    <g:layoutBody />
        <div class="footer">
          <div class="footerBlurb">
            <a href="http://memetix.com"><img style="border:0px;vertical-align: middle;margin:5px;" src="${resource(dir: 'images', file: 'badge.png')}" alt="Memetix Devworx, Inc."/></a>Gunshorten API © 2011 - Memetix Inc.
            <p/>Based on the grails-unshorten plugin by Jonathan Griggs <p/>(<a href="https://github.com/boatmeme/grails-unshorten">https://github.com/boatmeme/grails-unshorten</a>)
            
          </div>
        </div>
    </body>
</html>