var UbLoader = {};

UbLoader.playerprefix = "./uniboard-player";

UbLoader.init = function(playerprefix) {
   UbLoader.playerprefix = playerprefix;
   // prototype compat mode
   jQuery.noConflict();
}

UbLoader.launchPlayer = function(file, nbpages) {
        // setting full screen
        var myUbLoader = null;

        jQuery(document).ready(function() {
  		jQuery("body").append('<div id="sankoreplayer"><div id="sankoreplayer-header"><div><ul id="sankoreplayer-menulist"><li><a href="javascript:void(0)" onclick="UbLoader.closePlayer(); return false">Close</a></li></ul></div></div><iframe id="sankoreplayer-iframe" src="' + UbLoader.playerprefix + 'player.html#' + nbpages + ',' + file + '" width="100%" height="100%" border="0" frameborder="0"></iframe></div>');
 		jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbLoader.playerprefix + 'stylesheets/ubloader.css">');
        }); 

        jQuery(window).resize(function(){
             // console.log("Player loader resize started");
         
             if (jQuery("#sankoreplayer-menulist").length>0) {      
                 var top = jQuery("#sankoreplayer-header").height() + 6;
                 var currentHeight = jQuery("#sankoreplayer-iframe").height();
                 if (currentHeight != jQuery(window).height() - top) {
                    jQuery("#sankoreplayer-iframe").height(jQuery(window).height() - top);
                 }
             }
        }); 
        jQuery(window).resize();
} // end function launchPlayer

UbLoader.closePlayer = function() {
    // hidding the player
    jQuery("#sankoreplayer").remove();
}

