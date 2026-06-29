(function () {
  var repo = "Undercover-fabric/undercover-dumper-fabric";
  var host = window.location.hostname;
  var parts = window.location.pathname.split("/").filter(Boolean);

  if (host.endsWith(".github.io") && parts.length >= 1) {
    repo = host.replace(".github.io", "") + "/" + parts[0];
  }

  var src = document.getElementById("dl-src");
  if (src) {
    src.href = "https://github.com/" + repo;
  }

  var jar = document.getElementById("dl-jar");
  if (jar) {
    jar.href = "https://github.com/" + repo + "/releases/latest/download/ClassDumpAgent.jar";
  }
})();