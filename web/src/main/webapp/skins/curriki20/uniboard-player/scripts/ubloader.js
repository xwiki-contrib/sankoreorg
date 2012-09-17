var UbLoader = {};

UbLoader.playerprefix = "./uniboard-player";

UbLoader.init = function(playerprefix) {
   UbLoader.playerprefix = playerprefix;
   // prototype compat mode
   jQuery.noConflict();
}

UbLoader.loadCSS = function(url) {
 if(document.createStyleSheet) {
    try { document.createStyleSheet(url); } catch (e) { }
 } else {
    var el = jQuery("head");
    el.append('<link rel=stylesheet type="text/css" href="' + url + '">');
 }
}

UbLoader.launchPlayer = function(file, nbpages, mode, elementid) {
        // setting full screen
        var myUbLoader = null;

        UbLoader.mode = mode;
        UbLoader.elementid = elementid;
        jQuery(document).ready(function() {
                var el = jQuery(elementid);
  		el.append('<div id="sankoreplayer"><div id="sankoreplayer-header"><div><ul id="sankoreplayer-menulist"><li><a href="javascript:void(0)" onclick="UbLoader.closePlayer(); return false">Fermer</a></li></ul></div></div><iframe id="sankoreplayer-iframe" src="' + UbLoader.playerprefix + 'player.html#' + file + ',' + nbpages + ',' + mode + '" width="100%" height="100%" border="0" frameborder="0"></iframe></div>');
 		UbLoader.loadCSS(UbLoader.playerprefix + 'stylesheets/ubloader.css');
 		UbLoader.loadCSS(UbLoader.playerprefix + 'stylesheets/ubloader-' + mode + '.css');
        }); 

        jQuery(window).resize(function(){
          console.log("Player loader resize started");
          if (UbLoader.mode=="fs") {
             if (jQuery("#sankoreplayer-menulist").length>0) {      
                 var top = jQuery("#sankoreplayer-header").height() + 6;
                 var currentHeight = jQuery("#sankoreplayer-iframe").height();
                 if (currentHeight != jQuery(window).height() - top) {
                    jQuery("#sankoreplayer-iframe").height(jQuery(window).height() - top);

                 }
             }
           }
        }); 
        jQuery(window).resize();
} // end function launchPlayer

UbLoader.closePlayer = function() {
    // hidding the player
    jQuery("#sankoreplayer").remove();
    jQuery("body").css("overflow", "scroll");
}

UbLoader.switchMode = function(mode) {
    // alert("in switchMode " + mode + " " + UbLoader.elementid);
    var el = jQuery("#sankoreplayer");
    if (mode=="fs") {
      UbLoader.mode="fs";

      el.css("position", "fixed");
      el.css("top", "0");
      el.css("bottom", "0");
      el.css("left", "0");
      el.css("right", "0");
      
      // setting iframe height
      var top = jQuery("#sankoreplayer-header").height() + 6;
      var currentHeight = jQuery("#sankoreplayer-iframe").height();
      if (currentHeight != jQuery(window).height() - top) {
         jQuery("#sankoreplayer-iframe").height(jQuery(window).height() - top);
      }
    } else if (mode=="embed") {
      UbLoader.mode="embed";

      // reverting iframe manual full screen setting
      jQuery("#sankoreplayer-iframe").css("height", "");
      el.css("position", "relative");
      el.css("top", "");
      el.css("bottom", "");
      el.css("left", "");
      el.css("right", "");
    }
    jQuery(window).resize();
}
