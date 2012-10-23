var UbPlayer = {};

UbPlayer.playerprefix = "./uniboard-player";
UbPlayer.mode = "fs";

UbPlayer.messages = {
  "default" : {
    "widget.needsfs" : "This widget only works in full screen mode.",
    "widget.close" : "Close",
    "fs.close" : "Close"
   },
  "fr" : {
    "widget.needsfs" : "Ce widget ne fonctionne qu'en plein ecran.",
    "widget.close" : "Close",
    "fs.close" : "Close"
  }
}

UbPlayer.msg = function(key) {
 var messages = UbPlayer.messages["default"];
 if (UbPlayer.messages[UbPlayer.language]) {
   messages = UbPlayer.messages[UbPlayer.language];
 }
 if (messages[key]) {
  return messages[key];
 } else if (UbPlayer.messages["default"][key]) {
  return UbPlayer.messages["default"][key];
 } else {
  return "";
 }
}

UbPlayer.init = function(playerprefix) {
   UbPlayer.playerprefix = playerprefix;
}

UbPlayer.loadCSS = function(url) {
 if(document.createStyleSheet) {
    try { document.createStyleSheet(url); } catch (e) { }
 } else {
    var el = jQuery("head");
    el.append('<link rel=stylesheet type="text/css" href="' + url + '">');
 }
}

UbPlayer.launchPlayer = function(file, nbpages, mode, language) {
        console.log("Player opening");    

        var myUbPlayer = null;

        // set the mode to be used
        UbPlayer.startMode = mode;
        UbPlayer.mode = mode;
        UbPlayer.language = language;

        console.log("Player doc ready");    
        jQuery(document).ready(function(){

        // Update texts in player.html file
        jQuery("#quitFullScreen").text(UbPlayer.msg("fs.close"));
        jQuery("#app-viewer-close").text(UbPlayer.msg("widget.close"));

        var args = {
                documentData:{
                author:'Ludovic',
                authorEmail:'ludovic@xwiki.com',
                title:'Test',
                description:'Test',
                publishedAt:'Test',
                uuid:'0000',
                hasPdf:'false',
                hasUbz:'false',
                numberOfPages: nbpages,
                pagesBaseUrl: file 
            },
            pagesImg:[]
        };

        console.log("Player initial load start");    
        myUbPlayer = new UbPlayer.Player(args);
        console.log("Player initial load done");    

        // load specific stylesheets according to the window width
        UbPlayer.loadCSS(UbPlayer.playerprefix + 'stylesheets/master_' + mode + '.css');

        // TODO: fix detecting iPad and iPhone instead of safari
        /*
        if(jQuery.browser.safari) {
             UbPlayer.loadCSS(UbPlayer.playerprefix + 'stylesheets/master_ipad.css');
    
             addSwipeListener(document.body, function(e) {
                        if(e.direction=="right"){
                            myUbPlayer.goToPage("PREVIOUS");
                        }else if(e.direction=="left"){
                            myUbPlayer.goToPage("NEXT");
                        }
                    });
                    jQuery("#boards").addClass("boardsEnableAnimation");
         }
        */
 
        if(jQuery.browser.ie) { 
                console.log("Player ie mode");    
                UbPlayer.loadCSS(UbPlayer.playerprefix + 'stylesheets/master_ie.css');
        }

        jQuery(window).resize(function(){
            if (UbPlayer.mode=="fs") {
                console.log("Player resize started");    
                var ratioWh = myUbPlayer.currentPage.ratio;
                console.log("Player image ratio: " + ratioWh);    

                var paddingTop = jQuery("#head").is(":visible") ? jQuery("#head").height() : 0;
                var paddingBottom = jQuery("#foot").is(":visible") ? jQuery("#foot").height() : 0;

                console.log("Player start log");    
                console.log("Player paddingTop: " + paddingTop);    
                console.log("Player paddingBottom: " + paddingBottom);    
                console.log("Player resizeMode: " + myUbPlayer.resizeMode);    
                console.log("Player page width: " + jQuery(".page").width());    
                console.log("Player page height: " + jQuery(".page").height());    
                console.log("Player sankore body width: " + jQuery("#sankorebody").width());    
                console.log("Player sankore body height: " + jQuery("#sankorebody").height());    
                console.log("Player body width: " + jQuery("body").width());    
                console.log("Player body height: " + jQuery("body").height());    
                console.log("Player window width: " + jQuery(window).width());    
                console.log("Player windows height: " + jQuery(window).height());    

                if(myUbPlayer.resizeMode === "V"){
                console.log("before height: " + jQuery(".page").height());    
                jQuery(".page").height(jQuery("body").height() - paddingTop - paddingBottom - 50);
                jQuery(".page").css("margin-top", paddingTop + 25);
                console.log("after height: " + jQuery(".page").height());    
                jQuery(".page").width(jQuery(".page").height() * ratioWh);
                }else if(myUbPlayer.resizeMode === "H"){
                        jQuery(".page").width(jQuery("body").width() - 76);
                        jQuery(".page").height(jQuery(".page").width() * (1/ratioWh));
                }

                console.log("Player page width: " + jQuery(".page").width());    
                console.log("Player page height: " + jQuery(".page").height());    
                console.log("Player sankore body width: " + jQuery("#sankorebody").width());    
                console.log("Player sankore body height: " + jQuery("#sankorebody").height());  
                console.log("Player body width: " + jQuery("body").width());    
                console.log("Player body height: " + jQuery("body").height());    
                console.log("Player window width: " + jQuery(window).width());    
                console.log("Player windows height: " + jQuery(window).height());    

                if(jQuery("body").width() < (jQuery(".page").width() + 76) && myUbPlayer.resizeMode === "V"){
                    myUbPlayer.resizeMode = "H";
                    console.log("switch to H mode");
                    jQuery(window).resize();
                } else if(jQuery("body").height() < (jQuery(".page").height() + paddingTop + paddingBottom) && myUbPlayer.resizeMode === "H"){
                    myUbPlayer.resizeMode = "V";
                    console.log("switch to V mode");
                    jQuery(window).resize();
                };
             } // end if mode
             // force setting page-img size for IE8 standards mode
             jQuery("#page-img").width(jQuery(".page").width());
        });

        jQuery("#alert").click(function(){
            jQuery(this).hide();
        });

        // remove unnecessary items
        jQuery("#head-list-closeDescription").remove();
        jQuery("#head-list-share").remove();
        jQuery("#head-embed-box-left").remove();
     //   jQuery("#menu-button-export").remove();
     //   jQuery(".menu-box-right").remove();

        myUbPlayer.showHideThumbnails();

        // The current document has to be resized after the stylesheets have been loaded
        setTimeout(function() {
              jQuery("#head-embed").css({"display":"block"});
              jQuery("#sankorebody").css({"display":"block"});
              jQuery(window).resize();
              }, 500);

        console.log("Resize done");    
    }); //resize
                    
    console.log("Player activated");    
} // end function launchPlayer
