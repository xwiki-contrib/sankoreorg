var UbPlayer = {};

UbPlayer.playerprefix = "./uniboard-player";

UbPlayer.init = function(playerprefix) {
   UbPlayer.playerprefix = playerprefix;

   // prototype compat mode
   jQuery.noConflict();

   jQuery().ready(function(){
	jQuery.ajaxSetup({
		error:function(x,e){
			if(x.status==0){
			alert('You are offline!!\n Please Check Your Network.');
			}else if(x.status==404){
			alert('Requested URL not found.');
			}else if(x.status==500){
			alert('Internel Server Error.');
			}else if(e=='parsererror'){
			alert('Error.\nParsing JSON Request failed.');
			}else if(e=='timeout'){
			alert('Request Time out.');
			}else {
			alert('Unknow Error.\n'+x.responseText);
			}
		}
	});
   
        // load the player html
        var url = UbPlayer.playerprefix + 'player.html';
        var req = jQuery.get(url , function(data) {
          jQuery('#sankore').html(data);
         
        });
   });

   if (!window.console) console = {};
   console.log = console.log || function(){};
   console.log("start");
}

UbPlayer.reduceDomain = function(){
  
  if(typeof(document.domain) === "undefined")
    document.domain = "getuniboard.com";
  
  var allDomainsParts = document.domain.split(".");
  
  if (allDomainsParts.length > 2)
    document.domain = allDomainsParts[allDomainsParts.length - 2] + "." + allDomainsParts[allDomainsParts.length - 1];
}

UbPlayer.launchPlayer = function(file, nbpages) {
        console.log("Player opening");    

        // setting full screen
        this.makeFullScreen(jQuery("sankore"));

        jQuery("#sankore").css({ "display" : "block"});

        var myUbPlayer = null;

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

        myUbPlayer = new UbPlayer.Player(args);

            // load specific stylesheets according to the window width
        if(jQuery("#sankore").width() < 1000){
            console.log("Player mode embedded");    

            myUbPlayer.state = "embedded";
            if(!jQuery.browser.msie){
                jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master.css">');
                jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_embed.css">');
            }else{

            }
        } else {
            if(!jQuery.browser.msie){
                console.log("Player non ie mode");    

                jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master.css">');
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
                if(jQuery.browser.version != "8.0"){
                    jQuery("#alert").css({"display": "block"});
                    jQuery("#alert-background").animate({opacity:0.9},500);
                    jQuery("#alert-box").html('<p>You are running an old version of Internet Explorer. Please update your browser or install Google Chrome Frame.</p><br/><br/><p><a href="http://www.google.com/chromeframe">Download Google Chrome Frame</a></p></div>');
                }else{
                    jQuery("#alert").css({"display": "table"});
                    jQuery("#alert-background").animate({opacity:0.5},500);
                    jQuery("#alert-box").html('<p>Please start <b>Firefox, Safari</b> or <b>Chrome</b> to view this document, or </p><br/><p><a href="http://www.google.com/chromeframe">Download the Google Chrome Frame plugin</a></p><br/><p>to view it in Internet Explorer.</p><br/></br><p><a href="#">close</a></p></div>');
                }
            }
            jQuery("#menu-share-dropdown").appendTo("#document-links");
        }

        jQuery(window).resize(function(){
                console.log("Player resize started");    
                var ratioWh = myUbPlayer.currentPage.ratio;
                console.log("Player image ratio: " + ratioWh);    

                var paddingTop = jQuery("#head").height();
                var paddingBottom = jQuery("#foot").height();

                console.log("Player paddingTop: " + paddingTop);    
                console.log("Player paddingBottom: " + paddingBottom);    
                console.log("Player resizeMode: " + myUbPlayer.resizeMode);    
                console.log("Player page width: " + jQuery(".page").width());    
                console.log("Player page height: " + jQuery(".page").height());    
                console.log("Player sankore body width: " + jQuery("#sankorebody").width());    
                console.log("Player sankore body height: " + jQuery("#sankorebody").height());    
                console.log("Player sankore width: " + jQuery("#sankore").width());    
                console.log("Player sankore height: " + jQuery("#sankore").height());    
                console.log("Player body width: " + jQuery("body").width());    
                console.log("Player body height: " + jQuery("body").height());    
                console.log("Player window width: " + jQuery(window).width());    
                console.log("Player windows height: " + jQuery(window).height());    

                if(myUbPlayer.resizeMode === "V"){
                console.log("before height: " + jQuery(".page").height());    
                jQuery(".page").height(jQuery("#sankore").height()-paddingTop-paddingBottom-100);
                console.log("after height: " + jQuery(".page").height());    
                jQuery(".page").width(jQuery(".page").height() * ratioWh);
                }else if(myUbPlayer.resizeMode === "H"){
                        jQuery(".page").width(jQuery("#sankore").width() - 76);
                        jQuery(".page").height(jQuery(".page").width() * (1/ratioWh));
                }

                console.log("Player page width: " + jQuery(".page").width());    
                console.log("Player page height: " + jQuery(".page").height());    
                console.log("Player sankore body width: " + jQuery("#sankorebody").width());    
                console.log("Player sankore body height: " + jQuery("#sankorebody").height());  
                console.log("Player sankore width: " + jQuery("#sankore").width());    
                console.log("Player sankore height: " + jQuery("#sankore").height());      
                console.log("Player body width: " + jQuery("body").width());    
                console.log("Player body height: " + jQuery("body").height());    
                console.log("Player window width: " + jQuery(window).width());    
                console.log("Player windows height: " + jQuery(window).height());    

                if(jQuery("#sankore").width() < (jQuery(".page").width() + 76) && myUbPlayer.resizeMode === "V"){
                    myUbPlayer.resizeMode = "H";
                console.log("switch to H mode");
                    jQuery(window).resize();
                } else if(jQuery("#sankore").height() < (jQuery(".page").height() + paddingTop + paddingBottom) && myUbPlayer.resizeMode === "H"){
                    myUbPlayer.resizeMode = "V";
                console.log("switch to V mode");
                    jQuery(window).resize();
                };

                jQuery("#document-links").width(jQuery(".page").width());
        });

        jQuery("#alert").click(function(){
            jQuery(this).hide();
        });

        // remove unnecessary items
        jQuery("#head-list-closeDescription").remove();
        jQuery("#head-list-share").remove();
        jQuery("#head-embed-box-left").remove();
        jQuery("#menu-button-export").remove();
        jQuery(".menu-box-right").remove();

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


UbPlayer.closePlayer = function() {
    var targetElement = jQuery("sankore");

    // jQuery("#xwikimaincontainer").css({ "display" : "block"});

    // hidding the player
    jQuery("#sankore").css({ "display" : "none"});
    jQuery("#head-embed").css({"display":"none"});
    jQuery("#sankorebody").css({"display":"none"});
    console.log("Player closed");    
}

UbPlayer.makeFullScreen = function(targetElement) {
  // moving sankore element to top level
  var parent = targetElement.parent();
  if (parent != document.body) {
     jQuery('body').insertAfter(targetElement);
  } 

  // jQuery("#xwikimaincontainer").css({ "display" : "none"});
}


