<html>
    <head>
        <title>Gunshorten - Expand your URLs</title>
        <meta name="layout" content="main" />
    </head>
    <body>
    <div id="directions">
      Type in the URL you wish to Unshorten
    </div>
      <g:form name="shortUrlForm" controller="api" action="index">
        <input type="text" name="shortUrl" id="shortUrl" value="${params?.shortUrl}">
        <input id="submit" type="submit">
      </g:form>
      <g:if test="${urlObj}">
          <div id="results">
            <div class="result">
              <div class="label">Original URL</div>
              <div class="value">${urlObj.fullUrl}</div>
              <div class="clear"></div>
            </div>
            <div class="result">
              <div class="label">Short URL</div>
              <div class="value">${urlObj.shortUrl}</div>
              <div class="clear"></div>
            </div>
            <div class="result">
              <div class="label">Status</div>
              <div class="value">${urlObj.status}</div>
              <div class="clear"></div>
            </div>
            <div class="result">
              <div class="label">Cached</div>
              <div class="value">${urlObj.cached}</div>
              <div class="clear"></div>
            </div>
          </div>
      </g:if>
    </body>
</html>
