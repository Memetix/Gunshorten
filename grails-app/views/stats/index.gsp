<html>
    <head>
        <title>Gunshorten - Expand your URLs</title>
        <meta name="layout" content="main" />
    </head>
    <body>
    <div id="directions">
      Url Stats
    </div>
      <g:each var="urlObj" in="${shortUrls}">
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
            <div class="result">
              <div class="label">Count</div>
              <div class="value">${urlObj.count}</div>
              <div class="clear"></div>
            </div>
          </div>
      </g:each>
    </body>
</html>
