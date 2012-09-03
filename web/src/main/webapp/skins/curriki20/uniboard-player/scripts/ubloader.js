var UbLoader = {};

UbLoader.playerprefix = "./uniboard-player";

UbLoader.init = function(playerprefix) {
   UbLoader.playerprefix = playerprefix;
   // prototype compat mode
   jQuery.noConflict();
}

UbLoader.launchPlayer = function(file, nbpages, mode, elementid) {
        // setting full screen
        var myUbLoader = null;

        UbLoader.mode = mode;
        UbLoader.elementid = elementid;
        jQuery(document).ready(function() {
                var el = jQuery(elementid);
  		el.append('<div id="sankoreplayer"><div id="sankoreplayer-header"><div><ul id="sankoreplayer-menulist"><li><a href="javascript:void(0)" onclick="UbLoader.closePlayer(); return false">Close</a></li></ul></div></div><iframe id="sankoreplayer-iframe" src="' + UbLoader.playerprefix + 'player.html#' + file + ',' + nbpages + ',' + mode + '" width="100%" height="100%" border="0" frameborder="0"></iframe></div>');
 		el.append('<link rel=stylesheet type="text/css" href="' + UbLoader.playerprefix + 'stylesheets/ubloader.css">');
 		el.append('<link rel=stylesheet type="text/css" href="' + UbLoader.playerprefix + 'stylesheets/ubloader-' + mode + '.css">');
        }); 

        jQuery(window).resize(function(){
          // console.log("Player loader resize started");
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
}

UbLoader.switchMode = function(mode) {
    if (mode=="fs") {
      UbLoader.mode="fs";
    } else if (mode=="embed") {
      UbLoader.mode="embed";
      // reverting iframe manual full screen setting
      jQuery("#sankoreplayer-iframe").css("height", "");
    }
    var el = jQuery(UbLoader.elementid);
    // reverting css to correct mode
    el.append('<link rel=stylesheet type="text/css" href="' + UbLoader.playerprefix + 'stylesheets/ubloader-' + UbLoader.mode + '.css">');
    jQuery(window).resize();
}
