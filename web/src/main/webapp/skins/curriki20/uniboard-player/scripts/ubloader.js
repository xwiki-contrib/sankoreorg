var UbLoader = {};

UbLoader.playerprefix = "./uniboard-player";

UbLoader.init = function(playerprefix) {
  UbLoader.playerprefix = playerprefix;
  // prototype compat mode
  jQuery.noConflict();
}

UbLoader.resize = function() {
  var sankoreplayer = jQuery("#sankoreplayer");
  console.log("LOG: sankoreplayer height " + sankoreplayer.height());
  var sankoreheader = jQuery("#sankoreplayer-header");
  console.log("LOG: sankoreplayer-header height " + sankoreheader.outerHeight());
  var sankoreiframe = jQuery("#sankoreplayer-iframe");
  console.log("LOG: sankoreplayer-iframe height " + sankoreiframe.height());
  sankoreiframe.height(sankoreplayer.height() - sankoreheader.outerHeight());
  sankoreiframe.css("marginTop", sankoreheader.outerHeight());
  console.log("LOG: adjusted sankoreplayer-iframe height " + sankoreiframe.height());
  sankoreplayer.css("position", "fixed");
  jQuery("body").css("overflow", "hidden");
}

UbLoader.launchPlayer = function(file, nbpages) {
  
  jQuery(document).ready(function() {
    var sankoreiframe = jQuery("#sankoreplayer-iframe");
    sankoreiframe.attr("src", UbLoader.playerprefix + 'player.html#' + nbpages + ',' + file);
    jQuery("#sankore-preview").hide();
    jQuery("#sankoreplayer").show();
    //jQuery(window).resize();
    UbLoader.resize();
    
  });
} // end function launchPlayer

UbLoader.closePlayer = function() {
  // hidding the player  
  jQuery("#sankoreplayer").hide();
  jQuery("#sankore-preview").show();  
  jQuery("body").css("overflow", "auto");
}

