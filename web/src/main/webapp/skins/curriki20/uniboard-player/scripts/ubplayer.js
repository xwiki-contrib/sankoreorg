var UbPlayer = {};

UbPlayer.playerprefix = "./uniboard-player";
UbPlayer.mode = "fs";

UbPlayer.init = function(playerprefix) {
   UbPlayer.playerprefix = playerprefix;
}

UbPlayer.launchPlayer = function(file, nbpages, mode) {
        console.log("Player opening");    

        var myUbPlayer = null;

        // set the mode to be used
        UbPlayer.startMode = mode;
        UbPlayer.mode = mode;

        console.log("Player doc ready");    
        jQuery(document).ready(function(){

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
        jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_' + mode + '.css">');

        if(jQuery("body").width() < 1000){
            console.log("Player mode embedded");    

            myUbPlayer.state = "embedded";
            if(!jQuery.browser.msie){
                jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_embed.css">');
            }else{

            }
        } else {
            if(!jQuery.browser.msie){
                console.log("Player non ie mode");    

                if(jQuery.browser.safari) {
                    jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_ipad.css">');
                    addSwipeListener(document.body, function(e) {
                        if(e.direction=="right"){
                            myUbPlayer.goToPage("PREVIOUS");
                        }else if(e.direction=="left"){
                            myUbPlayer.goToPage("NEXT");
                        }
                    });
                    jQuery("#boards").addClass("boardsEnableAnimation");
                }
            }else{
                console.log("Player ie mode");    

                jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_ie.css">');
/*
                if(jQuery.browser.version != "8.0"){
                    jQuery("#alert").css({"display": "block"});
                    jQuery("#alert-background").animate({opacity:0.9},500);
                    jQuery("#alert-box").html('<span>You are running an old version of Internet Explorer. Please update your browser or install Google Chrome Frame. <a href="http://www.google.com/chromeframe">Download Google Chrome Frame</a></span>');
                }else{
                    jQuery("#alert").css({"display": "table"});
                    jQuery("#alert-background").animate({opacity:0.5},500);
                    jQuery("#alert-box").html('<span>Please start <b>Firefox, Safari</b> or <b>Chrome</b> to view this document, or <a href="http://www.google.com/chromeframe">Download the Google Chrome Frame plugin</a> to view it in Internet Explorer. (<a href="#">close</a>)</span>');
                }
*/
            }
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
