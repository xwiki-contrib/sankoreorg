var UbPlayer = {};

(function ($) {
$.fn.vAlign = function() {
return this.each(function(i){
var h = $(this).height();
console.log("LOG: vAlign height: " + h);
var oh = $(this).outerHeight();
console.log("LOG: vAlign outerHeight: " + oh);
var mt = (h + (oh - h)) / 2;
console.log("LOG: vAlign marginTop:" + mt);
$(this).css("margin-top", "-" + mt + "px");
$(this).css("top", "50%");
$(this).css("position", "absolute");
});
};
})(jQuery);

(function ($) {
$.fn.hAlign = function() {
return this.each(function(i){
var w = $(this).width();
console.log("LOG: hAlign width: " + w);
var ow = $(this).outerWidth();
console.log("LOG: hAlign outerWidth: " + ow);
var ml = (w + (ow - w)) / 2;
console.log("LOG: hAlign marginLeft: " + ml);
$(this).css("margin-left", "-" + ml + "px");
$(this).css("left", "50%");
$(this).css("position", "absolute");
});
};
})(jQuery);

UbPlayer.playerprefix = "./uniboard-player";

UbPlayer.myUbPlayer = null;

UbPlayer.init = function(playerprefix) {
  UbPlayer.playerprefix = playerprefix;
}

UbPlayer.resize = function() {
  if (UbPlayer.myUbPlayer) {
    console.log("LOG: Player resize started");
    var ratioWh = 1.6;
    console.log("LOG: Player image ratio: " + ratioWh);
    
    var head = jQuery("#head");
    var top = head.is(":visible") ? head.outerHeight() : 0;
    console.log("LOG: head height " + top);
    var foot = jQuery("#foot");
    var bottom = foot.is(":visible") ? foot.outerHeight() - 20 : 0;
    console.log("LOG: foot height " + bottom);
    var body = jQuery("body");
    console.log("LOG: body height " + body.height());
    var sankorebody = jQuery("#sankorebody");
    console.log("LOG: sankorebody height " + sankorebody.height());
    sankorebody.css("marginTop", top);
    
    var page = jQuery("#current-page");
    
    // setting sankorebody
    sankorebody.height(body.height() - top - bottom);
    console.log("LOG: adjusted sankorebody height " + sankorebody.height());
    
    // setting page      
    if (UbPlayer.myUbPlayer.resizeMode === "V") {
      console.log("LOG: page before height: " + page.height());
      page.height(sankorebody.height() - 48);
      console.log("LOG: page after height: " + page.height());
      page.width(page.height() * ratioWh);
      page.vAlign();
      page.hAlign();
    }
    else 
      if (UbPlayer.myUbPlayer.resizeMode === "H") {
        page.width(sankorebody.width() - 76);
        page.height(page.width() * (1 / ratioWh));
      }
  /*
   if(jQuery("body").width() < (jQuery(".page").width() + 76) && myUbPlayer.resizeMode === "V") {
   myUbPlayer.resizeMode = "H";
   console.log("switch to H mode");
   jQuery(window).resize();
   } else if (jQuery("body").height() < (jQuery(".page").height() + paddingTop + paddingBottom) && myUbPlayer.resizeMode === "H") {
   myUbPlayer.resizeMode = "V";
   console.log("switch to V mode");
   jQuery(window).resize();
   }*/
  }
}

jQuery(window).resize(function() {
  UbPlayer.resize();
});

UbPlayer.launchPlayer = function(file, nbpages) {
  console.log("Player opening");    

  jQuery(document).ready(function() {

    var args = {
      documentData: {
        author: 'Ludovic',
        authorEmail: 'ludovic@xwiki.com',
        title:'Test',
        description:'Test',
        publishedAt:'Test',
        uuid:'0000',
        hasPdf:'false',
        hasUbz:'false',
        numberOfPages: nbpages,
        pagesBaseUrl: file,
        format: 'ubz'
     },
     pagesImg:[]
    };

    UbPlayer.myUbPlayer = new UbPlayer.Player(args);

  // load specific stylesheets according to the window width
	/*
    if(jQuery("body").width() < 1000) {
      console.log("Player mode embedded");    

      myUbPlayer.state = "embedded";
      if(!jQuery.browser.msie) {
        jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_embed.css">');
      }else{

      }
    } else {
      if(!jQuery.browser.msie) {
        console.log("Player non ie mode");    

        if(jQuery.browser.safari) {
          jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_ipad.css">');
          addSwipeListener(document.body, function(e) {
            if(e.direction=="right") {
              myUbPlayer.goToPage("PREVIOUS");
            } else if(e.direction=="left") {
              myUbPlayer.goToPage("NEXT");
            }
          });
        jQuery("#boards").addClass("boardsEnableAnimation");
        }
      } else {
        console.log("Player ie mode");    

        jQuery("body").append('<link rel=stylesheet type="text/css" href="' + UbPlayer.playerprefix + 'stylesheets/master_ie.css">');

        if(jQuery.browser.version != "8.0") {
          jQuery("#alert").css({"display": "block"});
          jQuery("#alert-background").animate({opacity:0.9},500);
          jQuery("#alert-box").html('<span>You are running an old version of Internet Explorer. Please update your browser or install Google Chrome Frame. <a href="http://www.google.com/chromeframe">Download Google Chrome Frame</a></span>');
        } else {
          jQuery("#alert").css({"display": "table"});
          jQuery("#alert-background").animate({opacity:0.5},500);
          jQuery("#alert-box").html('<span>Please start <b>Firefox, Safari</b> or <b>Chrome</b> to view this document, or <a href="http://www.google.com/chromeframe">Download the Google Chrome Frame plugin</a> to view it in Internet Explorer. (<a href="#">close</a>)</span>');
        }
      }
    }*/

    

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
      //jQuery("#head-embed").css({"display":"block"});
      //jQuery("#sankorebody").css({"display":"block"});
      jQuery(window).resize();
    }, 500);

    console.log("Resize done");    
  }); //resize

  console.log("Player activated");    
} // end function launchPlayer
