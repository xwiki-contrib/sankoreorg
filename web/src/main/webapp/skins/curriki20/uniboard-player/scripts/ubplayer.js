var UbPlayer = {};

UbPlayer.reduceDomain = function(){
  
  if(typeof(document.domain) === "undefined")
    document.domain = "getuniboard.com";
  
  var allDomainsParts = document.domain.split(".");
  
  if (allDomainsParts.length > 2)
    document.domain = allDomainsParts[allDomainsParts.length - 2] + "." + allDomainsParts[allDomainsParts.length - 1];
}