
// called by widgets to make sure preferences are accessible
function loaded() {
 var iframe= document.getElementById('app-viewer-app');
 var iframewindow= iframe.contentWindow? iframe.contentWindow : iframe.contentDocument.defaultView;
 iframewindow.sankore = new UbPlayer.SankorePrefs(currentWidget.preferences);
}

UbPlayer.SankorePrefs = function(args) {
  this.preference = function(param, defaultValue) {
    if (args)
     return args[param];
    else
     return "";
  }
  this.setPreference = function(param, defaultValue) {
  }
};

UbPlayer.Player = function(args) {
  var that = this;

  this.firstPageNumber = 1;  
  this.thumbsBar = { 
    state:"min", 
    fullHeightVal:100, 
    minHeightVal:0, 
    sliding:false
  };
  this.viewer = new UbPlayer.Viewer();
  this.currentPage = { number:1, ratio:1.6 };
  this.state = "full";
  this.mode = "normal";
  this.resizeMode = "V";
  this.adaptPageTimer = null;
  this.sliderTimer = null;
  this.documentData = args.documentData;

  // verify if file is ubz or ubw
  if (this.documentData.pagesBaseUrl.indexOf(".ubz")!=-1)
   this.ubz = true;
  else
   this.ubz = false;

  this.pagesImg = args.pagesImg;
  this.thumbnails = {
    thumbsToHide:[],
    firstVisibleThumb:null,
    next:function(){
      // var yToCheck = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:first").position().top;
      var yToCheck = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:first").position().top;
      var foundFirstVisibleThumb = false;
      console.log("Thumbnails yToCheck: " + yToCheck);
      if (that.thumbnails.firstVisibleThumb!=null) {
        console.log("Thumbnails yToCheck using first visibleThumb position");
        yToCheck = that.thumbnails.firstVisibleThumb.position().top;
      }
      console.log("Thumbnails yToCheck: " + yToCheck);
      var counter = 0;
      jQuery("#thumbnails>#thumbnails-canvas>.thumbnail").each(function(){
        counter++;
        console.log("Thumbnails checking: " + counter + " " + jQuery(this).attr("page") + " " + jQuery(this).position().top + " " + jQuery(this).is(":visible"));
        if(jQuery(this).is(":visible")) {
         if(jQuery(this).position().top === yToCheck){
          console.log("Thumbnail position is first line " + jQuery(this).index());
          if(jQuery(this).index() === that.documentData.numberOfPages-1){
            console.log("In Last page");
            // jQuery("#thumbnail-next").addClass("disabled");
            that.thumbnails.thumbsToHide = [];
            return false;
          }
          console.log("Thumbnail should be hidden");
          jQuery("#thumbnail-previous").removeClass("disabled");
          that.thumbnails.thumbsToHide.push(jQuery(this));
         } else {
          console.log("Thumbnail is not first line. Found new first visible thumb");
          that.thumbnails.firstVisibleThumb = jQuery(this);
          return false;
         }
        }
      });
      console.log("Hiding thumbs to hide");
      for(var i=0; i<that.thumbnails.thumbsToHide.length; i++){
          console.log("Thumbnails hiding " + i);
          that.thumbnails.thumbsToHide[i].hide();
      }
      that.thumbnails.thumbsToHide = [];
    },
    previous:function(){
      var thumbIndex = 0;
      var currentThumb = null;
      if (that.thumbnails.firstVisibleThumb) {
      jQuery("#thumbnails>#thumbnails-canvas>.thumbnail").each(function(){
        thumbIndex++;
        currentThumb = jQuery(jQuery("#thumbnails>#thumbnails-canvas>.thumbnail")[that.thumbnails.firstVisibleThumb.index()-thumbIndex]);
        console.log("Thumbnails " + thumbIndex + " " + currentThumb.length);
        if(currentThumb.length > 0){
          jQuery("#thumbnail-next").removeClass("disabled");
          console.log("Thumbnail showing " + thumbIndex);
          currentThumb.show();
          console.log("Thumbnail position " + that.thumbnails.firstVisibleThumb.position().top + " " + currentThumb.position().top);
          if(that.thumbnails.firstVisibleThumb.position().top !== currentThumb.position().top) {
          console.log("Thumbnail showing finished " + that.thumbnails.firstVisibleThumb.position().top + " " + currentThumb.position().top);
            that.thumbnails.firstVisibleThumb = currentThumb;
            return false;
          }
        }else{
          // jQuery("#thumbnail-previous").addClass("disabled");
          return false;
        }
      });
     }
    }
  }

  // check if there is a page0. If yes switch firstPageNumber to page 0
  var page0url =  this.documentData.pagesBaseUrl + "/page000.json";
  jQuery.getJSON(page0url, function(data) {
      if(data){
        that.firstPageNumber = 0;
      }
  }).complete(function() {
  // Events binding  
  jQuery("#menu-button-first").click(function(){ that.goToPage("FIRST") });
  jQuery("#menu-button-previous").click(function(){ that.goToPage("PREVIOUS") });
  jQuery("#menu-button-index").toggle(function(){ that.showIndex() }, function(){ that.hideIndex() });
  jQuery("#menu-button-next").click(function(){ that.goToPage("NEXT") });
  jQuery("#menu-button-last").click(function(){ that.goToPage("LAST") });
  jQuery("#board-button-previous").click(function(){ that.goToPage("PREVIOUS") });
  jQuery("#board-button-next").click(function(){ that.goToPage("NEXT") });
  jQuery("#menu-button-index-embed").toggle(function(){ that.showIndex() }, function(){ that.hideIndex() });

  jQuery("#current-page")
    .bind("mouseleave", that.boardButtonOutHandler);
  jQuery("#boards")
    .bind("mouseenter", that.boardButtonOutHandler);

  jQuery("#thumbnail-next").click(function(){ console.log("calling next"); that.thumbnails.next()});
  jQuery("#thumbnail-previous").click(function(){that.thumbnails.previous()});
  
  jQuery("#menu-share-email").click(function(){that.showSharing()});
  jQuery("#menubottom-input").change(function(){ that.goToPage(jQuery("#menubottom-input").val()) });
  jQuery("#menu-button-showdetails")
    .toggle(
      function(){ that.showDescription() },
      function(){ that.hideDescription() });
  
  jQuery("#shareDoc")
   .toggle(
      function(){ jQuery("#menu-share-dropdown").show() },
      function(){ jQuery("#menu-share-dropdown").hide() });
  jQuery("#quitFullscreen")
    .click(
      function(){ that.switchToNormalMode() });
  jQuery("#quitDescription")
    .click(
      function(){ jQuery("#menu-button-showdetails").click() });
  jQuery("#menu-button-export")
    .toggle(
      function(){ jQuery("#menu-export-dropdown").show() },
      function(){ jQuery("#menu-export-dropdown").hide() })
    .hover(
      function(){ jQuery("#menu-button-export-left").css("background", "url(../images/menu-button-export-left-over.png)") },
      function(){ jQuery("#menu-button-export-left").css("background", "url(../images/menu-button-export-left.png)") });
  jQuery("#menu-list-share")
    .toggle(
      function(){ jQuery("#menu-share-dropdown").show() },
      function(){ jQuery("#menu-share-dropdown").hide() });
  jQuery("#menu-button-full").click(function(){ 
    that.switchToFullMode();
  });
  jQuery("#menu-button-showthumbnails").click(function() { that.showHideThumbnails(); });

  // Add the thumbnails
  for(var i=0; i<that.documentData.numberOfPages; i++){
    var formattedThumbNumber = that.formatPageNumber(i+that.firstPageNumber);
    var thumburl = that.documentData.pagesBaseUrl + "/page" + formattedThumbNumber + ".thumbnail.jpg";
    var thumbhtml = '<div class="thumbnail"><img class="thumb-img" src="' + thumburl + '" title="page ' + (i+that.firstPageNumber) + '" alt="thumbnail" height="100%" width="auto"/></div>';
    jQuery("#thumbnails>#thumbnails-canvas").append(thumbhtml); 
  }
  jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:first").remove();

  jQuery(".thumbnail")
    .hover(
      function(){ jQuery(this).addClass("selected") },
      function(){ jQuery(this).removeClass("selected") })
    .click(function(){
      that.goToPage(jQuery(this).index()+that.firstPageNumber);
      if(jQuery("#description").css("display") != "none"){
        jQuery("#menu-button-showdetails").click();
        jQuery(window).resize();
      }
    });

  jQuery("#description-text").append("<a href='mailto:" + that.documentData.authorEmail + "'>" + that.documentData.author + 
                                      "</a><br/>" + that.documentData.title + 
                                      "<br/>" + that.formatDate(that.documentData.publishedAt) + 
                                      "<br/><br/>" + that.documentData.description);
  
  that.openPage(1);
  // Load the images
  for(var i=0; i<that.documentData.numberOfPages; i++){
    that.pagesImg[i] = new Image();
    that.pagesImg[i].src = that.documentData.pagesBaseUrl + "/page" + that.formatPageNumber(i+that.firstPageNumber) + (this.ubz ? ".thumbnail" : "") + ".jpg";
  }
  });
};

UbPlayer.Player.prototype.showHideThumbnails = function() {
    var newFootHeight = 0;
    var easing = "";
    var that = this;
    var thumbnailBaseHeight = 0;
    var footerBaseHeight = 64;
    var footerWidth = 400;
    var bodyBasePadding;
 
    if(that.thumbsBar.state === "min"){
      newFootHeight = that.thumbsBar.fullHeightVal;
      that.thumbsBar.state = "full";
      easing = "easeOutBack";
    } else {
      newFootHeight = that.thumbsBar.minHeightVal;
      that.thumbsBar.state = "min";
      easing = "easeInQuint";
    }
  
    if(that.thumbsBar.state === "full"){
      jQuery("#body").css({paddingBottom: bodyBasePadding + newFootHeight });
      jQuery(window).resize();
    }
    jQuery("#thumbnails").animate(
      {height: thumbnailBaseHeight + newFootHeight},
      footerWidth,
      easing,
      function(){ 
        jQuery("#body").css({paddingBottom: bodyBasePadding + newFootHeight });
        jQuery(window).resize();
      }
    );

    jQuery("#foot").animate(
      {height: footerBaseHeight + newFootHeight, marginTop:-footerBaseHeight - newFootHeight},
      footerWidth,
      easing
    );
}

UbPlayer.Player.prototype.sliderListener = function(currentPageNmbr){
  var that = this;
  var slider = jQuery("#thumbnails-slider-handler");
  var unitWidth = slider.parent("div").width();
  var sliderIndex = 1 + Math.floor((slider.position().left + ((currentPageNmbr-1) * unitWidth)) / unitWidth);	      
  if(sliderIndex !== this.currentPage.number) this.goToPage(sliderIndex);
  this.sliderTimer = setTimeout(function(){that.sliderListener(currentPageNmbr)}, 10);
}

UbPlayer.Player.prototype.boardButtonOutHandler = function(event){
  if( event.pageY >= jQuery("#boards").position().top && 
      event.pageY <= (jQuery("#boards").position().top + jQuery("#boards").height()) ){
    if(event.pageX >= (jQuery("#current-page").position().left + jQuery("#current-page").width())){
      jQuery("#board-button-next").stop().animate({opacity:1}, 200);
    }else if(event.pageX < jQuery("#current-page").position().left){
      jQuery("#board-button-previous").stop().animate({opacity:1}, 200);
    }
  }
}

UbPlayer.Player.prototype.goToPage = function(pageNumber){
  var checkPoint = { finish:"%", start:"%" };
  var that = this;
  
  if(pageNumber === "NEXT"){
    pageNumber = this.currentPage.number + 1;
  }else if(pageNumber === "PREVIOUS"){
    pageNumber = this.currentPage.number - 1;
  }else if(pageNumber === "FIRST") {
    pageNumber = this.firstPageNumber; 
  }else if(pageNumber === "LAST") {
    pageNumber = this.documentData.numberOfPages + 1 - this.firstPageNumber;
  }

  if(pageNumber > this.currentPage.number){
      checkPoint = { finish:"-200%", start:"100%" };
  }else if(pageNumber < this.currentPage.number){
      checkPoint = { finish:"100%", start:"-200%" };
  }else{
      return 0;
  }
  this.currentPage.number = pageNumber;

  if(this.currentPage.number < this.firstPageNumber){
    this.currentPage.number = this.firstPageNumber;
  }else if(this.currentPage.number > this.documentData.numberOfPages){
    this.currentPage.number = this.documentData.numberOfPages;
  }
	
	jQuery("#current-page")
	  .unbind("mouseenter")
	  .unbind("mouseleave");
	  
	this.viewer.hide();
	
	if(!jQuery.browser.safari){ // JS animation if not safari
    jQuery("#boards").stop().animate(
      {marginLeft:checkPoint.finish},
      300,
      "easeInQuint",
      function(){
        that.openPage(that.currentPage.number);
        jQuery("#thumbnails").css({width: jQuery("#thumbnails").width()});
        jQuery("#boards").css({ marginLeft:checkPoint.start });
        jQuery("#boards").animate(
          {marginLeft:"0"},
          300,
          "easeOutQuint",
          function(){
            jQuery("#thumbnails").css({width: "auto"});
            jQuery("#current-page")
              .bind("mouseleave", that.boardButtonOutHandler);
          }
        );
      }
    );
  }else{ // CSS animations if safari
    function boardsAnimStart(){
      that.openPage(that.currentPage.number);
      jQuery("#thumbnails").css({width: jQuery("#thumbnails").width()});
      jQuery("#boards").css("-webkit-transition-duration", "0ms");
      jQuery("#boards").css({marginLeft:checkPoint.start});
    }
    
    function boardsAnimStop(){
      jQuery("#boards").css("-webkit-transition-duration", "0ms");
      jQuery("#boards").css("-webkit-transition-timing", "ease-out");
      jQuery("#boards").css({marginLeft:0});
    }
    
    function boardsAnimEnd(){
      jQuery("#thumbnails").css({width: "auto"});
      jQuery("#current-page")
        .bind("mouseleave", that.boardButtonOutHandler);
    }
    
    jQuery("#boards").css({marginLeft:checkPoint.finish});
    boardsAnimStart();
    boardsAnimStop();
    boardsAnimEnd();
  }
}

UbPlayer.Player.prototype.adaptPage = function(){
  jQuery(window).resize();
  this.adaptPageTimer = setTimeout(function(){adaptPage()}, 10);
}

UbPlayer.Player.prototype.switchToFullMode = function(){
  jQuery(".board-button-unit").css("display", "block");
  jQuery("#foot").hide();
  jQuery("#head-list-share").css("display", "none");
  jQuery("#head-list-close").css("display", "inline-block");
  this.mode = "full";
 
  // switching parent to full screen mode
  if (UbPlayer.startMode=="embed" && parent.UbLoader) {
     UbPlayer.mode = "fs"; 
     parent.UbLoader.switchMode("fs");
     // load full screen css
     UbPlayer.loadCSS(UbPlayer.playerprefix + 'stylesheets/master_fs.css');
     // forcing body size to make sure it's done before the resize call
     jQuery("body").css("position", "fixed");
     jQuery("body").css("overflow", "hidden");
     jQuery("body").css("left", "0");
     jQuery("body").css("right", "0");
     jQuery("body").css("top", "0");
     jQuery("body").css("bottom", "0");
     jQuery(window).resize();
  }
}

UbPlayer.Player.prototype.switchToNormalMode = function(){
  jQuery(".board-button-unit").css("display", "none");
  jQuery("#foot").show();
  jQuery("#head-list-share").css("display", "inline-block");
  jQuery("#head-list-close").css("display", "none");
  /*
  if(this.thumbsBar.state === "full"){
  	console.log("here");
    jQuery("#body").css({paddingBottom:110+this.thumbsBar.fullHeightVal});
    jQuery("#body").css({paddingBottom: 0});
  }else{
  	console.log("here2");
    jQuery("#body").css({paddingBottom:110});
  }
  */
  this.mode = "normal";

  // switching parent back to embed mode
  if (UbPlayer.startMode=="embed" && parent.UbLoader) {
     UbPlayer.mode = "embed"; 
     parent.UbLoader.switchMode("embed");

     // load embed css
     UbPlayer.loadCSS(UbPlayer.playerprefix + 'stylesheets/master_embed.css');
     // revert manual size setting on current-page div
     jQuery("#current-page").css("width", "");
     jQuery("#current-page").css("height", "");
     jQuery("#page-img").width("");
  }
}

UbPlayer.Player.prototype.showSharing = function(){
  jQuery("#boards").animate({marginTop:"100%"}, function(){
     jQuery(this).hide();
     jQuery("#sharing").show();
   });
}

UbPlayer.Player.prototype.hideSharing = function(){

}

UbPlayer.Player.prototype.showDescription = function(){
  jQuery("#boards").animate({marginTop:"-100%"}, function(){
    jQuery(this).hide();
    jQuery("#description").show();
    jQuery("#head-list-share").css("display", "none");
    jQuery("#head-list-closeDescription").css("display", "inline-block");
  });
  
  jQuery("#menu-button-full")
    .addClass("disabled")
    .unbind("click");
  jQuery("#menu-button-previous")
    .addClass("disabled")
    .unbind("click");
  jQuery("#menu-button-next")
    .addClass("disabled")
    .unbind("click");
  jQuery("#menu-button-index")
    .addClass("disabled")
    .unbind("toggle")
    .unbind("click");
}

UbPlayer.Player.prototype.hideDescription = function(){
  var that = this;
  
  jQuery("#description").animate({marginTop:"100%"}, function(){
    jQuery(this)
      .hide()
      .css({marginTop:0});
    jQuery("#boards")
      .show()
      .animate({marginTop:"0px"});
    jQuery("#head-list-share").css("display", "inline-block");
    jQuery("#head-list-closeDescription").css("display", "none");
    jQuery(window).resize();
    jQuery(window).resize();
  });
  
  jQuery("#menu-button-full")
    .removeClass("disabled")
    .bind("click", function(){that.switchToFullMode()});
  jQuery("#menu-button-previous")
    .removeClass("disabled")
    .bind("click", function(){that.goToPage("PREVIOUS")});
  jQuery("#menu-button-next")
    .removeClass("disabled")
    .bind("click", function(){that.goToPage("NEXT")});
  jQuery("#menu-button-index")
    .removeClass("disabled")
    .toggle(function(){ that.showIndex() }, function(){ that.hideIndex() });
}

UbPlayer.Player.prototype.showIndex = function(){
  var that = this;
  var thumbsPerRow = Math.round(Math.sqrt(this.documentData.numberOfPages));

  jQuery("#menu-button-previous")
    .addClass("disabled")
    .unbind("click");
  jQuery("#menu-button-next")
    .addClass("disabled")
    .unbind("click");

  jQuery("#boards").animate({marginTop:"-100%"}, function(){
    jQuery(this).hide();
    jQuery("#index")
      .show()
      .empty();
    that.drawIndexThumbnails(thumbsPerRow);
  });
}

UbPlayer.Player.prototype.hideIndex = function(){
  var that = this;
  
  jQuery("#menu-button-previous")
    .removeClass("disabled")
    .bind("click", function(){that.goToPage("PREVIOUS")});
  jQuery("#menu-button-next")
    .removeClass("disabled")
    .bind("click", function(){that.goToPage("NEXT")});
    
  jQuery("#index").hide();
  jQuery("#boards")
    .show()
    .animate({marginTop:"0px"});
}

UbPlayer.Player.prototype.drawIndexThumbnails = function(thumbsPerRow){
  var that = this;
  var newIndex = jQuery("#index>div").length + 1;
  var newIndexThumbnailNumber = this.formatPageNumber(newIndex);
  var newIndexThumbnail = jQuery("<div class='thumbnail index'><img class='thumb-img' src='" + this.documentData.pagesBaseUrl + "/page" + 
                          newIndexThumbnailNumber + 
                          ".thumbnail.jpg' width='auto' height='100%'/></div>");
  newIndexThumbnail
    .hover(
      function(){ if(newIndex !== that.currentPage.number) jQuery(this).addClass("selected") },
      function(){ jQuery(this).removeClass("selected") })
    .click(function(){
      that.goToPage(newIndex);
      jQuery("#menu-button-index").click();
      jQuery(window).resize();
    });
    
  jQuery("#index").append(newIndexThumbnail);
  if(newIndex === this.currentPage.number){ newIndexThumbnail.addClass("current") }
  if((newIndex)%thumbsPerRow === 0)jQuery("#index").append("<br/>");
  if(jQuery("#index>div").length < this.documentData.numberOfPages) setTimeout(function(){that.drawIndexThumbnails(thumbsPerRow)}, 100);
}

UbPlayer.Player.prototype.formatPageNumber = function(pageNumber){
  var formattedPageNumber = ("00" + pageNumber).substr(("00" + pageNumber).length-3, 3);
  return formattedPageNumber;
}

UbPlayer.Player.prototype.formatDate = function(date){
  var months = new Array("January", "February", "March", "April", "May", "June", 
                         "July", "August", "September", "October", "November", "December");
  var formattedDate = "";
  formattedDate = this.documentData.publishedAt.split(" ")[0].split("-");
  formattedDate = formattedDate[2] + " " + months[parseInt(formattedDate[1])-1] + " " + formattedDate[0];
  return formattedDate;
}

UbPlayer.Player.prototype.openPage = function(pageNumber){
  var that = this;
  var formattedPageNumber = this.formatPageNumber(pageNumber);
  var fileName = this.documentData.pagesBaseUrl + "/page" + formattedPageNumber  + (this.ubz ? ".thumbnail" : "") + ".jpg";
  var jsonName = this.documentData.pagesBaseUrl + "/page" + formattedPageNumber + ".json";
                       
  this.currentPage.number = pageNumber;

  jQuery(".appImg").remove();
  jQuery("#menubottom-input").val(pageNumber);
  jQuery("#thumbnails-slider>div").removeClass("current");
  jQuery(jQuery("#thumbnails-slider>div")[pageNumber-1]).addClass("current");
  //jQuery("#thumbnails-canvas>div").removeClass("current");
  //jQuery(jQuery("#thumbnails-canvas>div")[pageNumber-1]).addClass("current");
  
  jQuery("#current-page>img").attr("src", fileName);
  
  // Slider handler
  if(this.thumbsBar.sliding)
    jQuery("#thumbnails-slider-handler")
      .appendTo(jQuery("#thumbnails-slider>div")[pageNumber-1])
      .html(pageNumber);
    
  jQuery.getJSON(jsonName, function(data) {
      if(data){
        var scene = {
          x:parseFloat(data.scene.x),
          y:parseFloat(data.scene.y),
          width:parseFloat(data.scene.width),
          height:parseFloat(data.scene.height)
        }
        var widget = {};
        var app = {};
        
        that.currentPage.ratio = scene.width / scene.height;
        
        for(var i in data.widgets){
          widget = data.widgets[i];
          app = {
            src:widget.startFile.indexOf("http://") === -1 ? that.documentData.pagesBaseUrl
                + "/" + widget.src + "/" + widget.startFile : widget.startFile,
            img:{
              src:that.documentData.pagesBaseUrl + "/widgets/" + widget.uuid + ".png",
              widthInPercent:parseFloat(widget.width) / scene.width * 100,
              heightInPercent:parseFloat(widget.height) / scene.height * 100,
              leftInPercent:(parseFloat(widget.x) + Math.abs(scene.x)) / scene.width * 100,
              topInPercent:(parseFloat(widget.y) + Math.abs(scene.y)) / scene.height * 100,
              node:jQuery("<div class='appImg'></div>")
            }
          };

          app.img.node
            .css({
              position:"absolute",
              width:app.img.widthInPercent + "%",
              height:app.img.heightInPercent + "%",
              top:app.img.topInPercent + "%",
              left:app.img.leftInPercent + "%"})
            .click(function(app, widget){
              return function(e){
                currentWidget = widget;
                if (that.mode!="full" && widget.width>600) {
                   alert(UbPlayer.msg("widget.needsfs"));
                } else {
                   that.viewer.show(app.src, widget.nominalWidth, widget.nominalHeight);
                }
              }
            }(app, widget))
            .hover(
              function(){
                jQuery(this)
                  .css({ backgroundImage:"url(../images/app-img-bck.png)" })
                jQuery("#app-border")
                  .appendTo(jQuery(this))
                  .show();
                jQuery("#app-border-middle>img")
                  .animate({opacity:1});
              },
              function(){
                jQuery(this)
                  .css({ backgroundImage:"none" })
                jQuery("#app-border")
                  .hide()
                  .appendTo(jQuery("#current-page"));
                jQuery("#app-border-middle>img")
                  .css({opacity:0});
              }
            )
            .appendTo(jQuery("#current-page"));
            
          var showAppImg = jQuery("<img/>");
          showAppImg
            .attr("src", "../images/app-view-start.png")
            .css({
              opacity:0,
              position:"absolute",
              top:-18,
              left:"50%",
              marginLeft:-10})
            .appendTo(app.img.node);
          
            setTimeout(
              function(showAppImg){
                return function(){
                  showAppImg.animate({opacity:1},function(){
                    setTimeout(function(){showAppImg.animate({opacity:0},function(){showAppImg.remove()})},1000);
                  })
                }
              }(showAppImg)
              ,(parseInt(i)+1)*500
            );          
        }
      }
  });
  
  jQuery(window).resize();

  // Disable previous button if the current page is 1
  if(pageNumber === 1){
    jQuery("#menu-button-previous").unbind("click");
    jQuery("#board-button-previous").unbind("click");
  }else{ // Enable previous button if it has no click event
    if(!jQuery("#menu-button-previous").data("events")){
      jQuery("#menu-button-previous").bind("click", function(){that.goToPage("PREVIOUS")});
      jQuery("#board-button-previous").bind("click", function(){that.goToPage("PREVIOUS")});
    }
  }
  
  // Disable next button if the current page is the last page
  if(pageNumber === this.documentData.numberOfPages){
    jQuery("#menu-button-next").unbind("click");
    jQuery("#board-button-next").unbind("click");
  }else{ // Enable previous button if it hasn't any click event
    if(!jQuery("#menu-button-next").data("events")){
      jQuery("#menu-button-next").bind("click", function(){that.goToPage("NEXT")});
      jQuery("#board-button-next").bind("click", function(){that.goToPage("NEXT")});
    }
  }
}
