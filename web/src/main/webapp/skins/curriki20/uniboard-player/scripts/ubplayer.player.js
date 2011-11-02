UbPlayer.Player = function(args) {
  var that = this;
  
  this.viewer = new UbPlayer.Viewer();
  this.currentPage = { number:1, ratio:1.6 };
  this.state = "full";
  this.mode = "normal";
  this.resizeMode = "V";
  this.adaptPageTimer = null;
  this.sliderTimer = null;
  this.documentData = args.documentData;
  this.pagesImg = args.pagesImg;
  this.pagesJson = [];
  
  // Load the images
  for(var i=1; i<=this.documentData.numberOfPages; i++){
    this.pagesImg[i] = new Image();
    this.pagesImg[i].src = this.documentData.pagesBaseUrl + "/page" + this.formatPageNumber(i) + ".thumbnail.jpg";
    this.pagesJson[i] = this.documentData.pagesBaseUrl + "/page" + this.formatPageNumber(i) + ".json";
  }
  
  this.thumbnails = {
    state: "normal", 
    height:0,
    fullHeight:100, 
    sliding:false,
    thumbsToHide:[],
    firstVisibleThumb:null,
    init: function() {
      var thumbnails = this;
      this.addThumbnails();
      jQuery("#menu-button-showthumbnails").unbind("click").bind("click", function(){
        thumbnails.toggle()
      });
      this.update();
    },
    addThumbnails: function() {
      var newThumbnail = null;      
      for(var i=1; i<=that.documentData.numberOfPages; i++){
        newThumbnail = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:first").clone();        
        newThumbnail
          .find("img")
          .attr("src", that.pagesImg[i].src)
          .attr("title", "page " + (i));
        jQuery("#thumbnails>#thumbnails-canvas>div:last-child").after(newThumbnail);
        newThumbnail
          .hover(
            function(){ jQuery(this).addClass("selected") },
            function(){ jQuery(this).removeClass("selected") })
          .click(function(){            
            that.goToPage(jQuery(this).index() + 1);          
          }); 
      }
      jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:first").remove();
      this.firstVisibleThumb = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:first");
    },
    toggle: function() {
      if (this.state === "normal")
        this.switchToFullMode();
      else
        this.switchToNormalMode();
    },
    switchToFullMode: function() {
      if(this.state === "normal") {
        this.state = "full";           
        this.animate(this.fullHeight, "easeOutBack");
        this.update();    
      }
    },
    switchToNormalMode: function() {
      if(this.state === "full") {
        this.state = "normal";
        this.animate(this.height, "easeInQuint");     
      }
    },
    animate: function(height, easing) {
      jQuery("#thumbnails").animate(
        {height:50 + height},
        400,
        easing,
        function() { 
          jQuery(window).resize();
        }
      );
      jQuery("#foot").animate(
        {height:96 + height, marginTop:-height},
        400,
        easing,
        function() {
          jQuery(window).resize();
        }
      );
    },
    next:function(){
      var visibleThumbs = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:visible");
      var yToCheck = jQuery(visibleThumbs[0]).position().top;
      var lToCheck = yToCheck;
      visibleThumbs.each(function(){
        if(jQuery(this).position().top === yToCheck){
          that.thumbnails.thumbsToHide.push(jQuery(this));
        }else{
          return false;
        }
      });      
      for(var i=0; i<that.thumbnails.thumbsToHide.length; i++){
        that.thumbnails.thumbsToHide[i].hide();
      }
      var lToCheck = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:last").position().top;
      if (yToCheck === lToCheck) {
        jQuery("#thumbnail-next").addClass('disabled').unbind("click");
      } else {
        jQuery("#thumbnail-next").removeClass('disabled').unbind("click").bind("click", function(){that.thumbnails.next();});
      }
      jQuery("#thumbnail-previous").removeClass('disabled').unbind("click").bind("click", function(){that.thumbnails.previous();});
      that.thumbnails.thumbsToHide = [];
    },
    previous:function(){
      var hiddenThumbs = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:hidden");
      var firstVisibleThumb = jQuery(jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:visible")[0]);
      var yToCheck = firstVisibleThumb.position().top;
      jQuery(hiddenThumbs.get().reverse()).each(function(){
        jQuery(this).show();
        if (yToCheck < firstVisibleThumb.position().top) {
          return false;
        }
      });
      if (jQuery("#thumbnails>#thumbnails-canvas>.thumbnail:hidden").length > 0) {
        jQuery("#thumbnail-previous").removeClass('disabled').unbind("click").bind("click", function(){that.thumbnails.previous();});
      } else {
        jQuery("#thumbnail-previous").addClass('disabled').unbind("click");
      }
      jQuery("#thumbnail-next").removeClass('disabled').unbind("click").bind("click", function(){that.thumbnails.next();});
    },
    update: function() {
      // reset hidden, show all thumbnails
      jQuery("#thumbnails>#thumbnails-canvas>.thumbnail").show();
      jQuery("#thumbnails>#thumbnails-canvas>.thumbnail.current").removeClass("current");
      var visibleThumbs = jQuery("#thumbnails>#thumbnails-canvas>.thumbnail");
      var currentThumb = jQuery(visibleThumbs[that.currentPage.number-1]).addClass("current");
      var yToCheck = currentThumb.position().top;
      var lToCheck = jQuery(visibleThumbs[visibleThumbs.length-1]).position().top;
      visibleThumbs.each(function(){
        if (jQuery(this).position().top < yToCheck) {
          that.thumbnails.thumbsToHide.push(jQuery(this));
        } else {
          return false;
        }
      });
      if (yToCheck === lToCheck) {
        jQuery("#thumbnail-next").addClass('disabled').unbind("click");
      } else {
        jQuery("#thumbnail-next").removeClass('disabled').unbind("click").bind("click", function(){that.thumbnails.next();});
      }
      if (that.thumbnails.thumbsToHide.length > 0) {
        jQuery("#thumbnail-previous").removeClass('disabled').unbind("click").bind("click", function(){that.thumbnails.previous();});
      } else {
        jQuery("#thumbnail-previous").addClass('disabled').unbind("click");
      }
      for(var i=0; i<that.thumbnails.thumbsToHide.length; i++){
        that.thumbnails.thumbsToHide[i].hide();
      }
      that.thumbnails.thumbsToHide = [];
    }
  }
  this.thumbnails.init();
  
  this.indexThumbnails = {
    visible: false,
    thumbsPerRow: Math.round(Math.sqrt(that.documentData.numberOfPages)),
    init: function() {
      this.addIndexThumbnails();
      this.enable();
    },
    addIndexThumbnails: function() {
      var newThumbnail = null;      
      for(var i=1; i<=that.documentData.numberOfPages; i++){
        newThumbnail = jQuery("#index>.thumbnail:first").clone();        
        newThumbnail
          .find("img")
          .attr("src", that.pagesImg[i].src)
          .attr("title", "page " + (i));          
        jQuery("#index>:last-child").after(newThumbnail);
        newThumbnail
          .hover(
            function(){ jQuery(this).addClass("selected") },
            function(){ jQuery(this).removeClass("selected") })
          .click(function(){
            that.indexThumbnails.hide();
            that.goToPage(jQuery(this).index() - Math.floor((jQuery(this).index()+1)/(that.indexThumbnails.thumbsPerRow+1)) + 1);          
          });     
        if (i === that.currentPage.number){ newThumbnail.addClass("current") }
        if ((i)%this.thumbsPerRow === 0) jQuery("#index").append("<br/>");        
      }
      jQuery("#index>.thumbnail:first").remove();                
    },
    show: function() {
      if (!this.visible) {
        that.fullscreen.disable();
        that.description.disable();
        jQuery("#boards").animate({marginTop: "-100%"}, 
          function(){
            jQuery(this).hide();
            jQuery("#index").show();
          });
        jQuery("#index").height(jQuery("#sankorebody").height());
        this.visible = true;
     }
    },
    hide: function() {
      if (this.visible) {
        that.fullscreen.enable();
        that.description.enable();
        jQuery("#index").hide();
        jQuery("#boards")
          .show()
          .animate({marginTop: "0px"});
        this.visible = false;
      }
    },
    toggle: function() {
      if (this.visible)
        this.hide();
      else 
        this.show();
    },
    update: function() {
      jQuery("#index").height(jQuery("#sankorebody").height());
      jQuery("#index>div").removeClass("current");
      jQuery(jQuery("#index>div")[that.currentPage.number-1]).addClass("current");
    },
    enable: function() {
      var indexThumbnails = this;
      jQuery("#menu-button-index").removeClass("disabled").unbind("click").bind("click", function(){
        indexThumbnails.toggle();
      });
    },
    disable: function() {
      jQuery("#menu-button-index").addClass("disabled").unbind("click");      
    }
  }
  this.indexThumbnails.init();
  
  this.description = {
    visible: false,
    init: function() {
      this.enable();        
    },
    show: function() {
      if (!this.visible) {
        jQuery("#boards").animate({
          marginTop: "-100%"
        }, function(){
          jQuery(this).hide();
          jQuery("#description").show();
          //jQuery("#head-list-share").css("display", "none");
          //jQuery("#head-list-closeDescription").css("display", "inline-block");
        });
        that.fullscreen.disable();        
        that.indexThumbnails.hide();
        that.indexThumbnails.disable();
        that.navigation.disable();
        this.visible = true;
      }
    },
    hide: function() {
      if (this.visible) {
        jQuery("#description").animate({
          marginTop: "100%"
        }, function(){
          jQuery(this).hide().css({
            marginTop: "30px"
          });
          jQuery("#boards").show().animate({
            marginTop: "0px"
          });
          //jQuery("#head-list-share").css("display", "inline-block");
          jQuery("#head-list-closeDescription").css("display", "none");
          jQuery(window).resize();          
        });
        that.fullscreen.enable();
        that.indexThumbnails.enable();
        that.navigation.enable();
        this.visible = false;
      }
    },
    toggle: function() {
      if (this.visible)
        this.hide();
      else
        this.show();
    },
    enable: function() {
      var description = this;
      jQuery("#menu-button-showdetails").removeClass("disabled").unbind("click").bind("click", function(){description.toggle()});
    },
    disable: function() {
      jQuery("#menu-button-showdetails").addClass("disabled").unbind("click");      
    }
  }
  this.description.init();
  
  this.navigation = {
    init: function() {
      this.update();
    },
    enable: function() {
      this.update();
    },
    disable: function() {
      jQuery("#menu-button-previous, #board-button-previous").addClass('disabled').unbind("click");
      jQuery("#menu-button-next, #board-button-next").addClass('disabled').unbind("click");
    },
    update: function() {
      if (that.currentPage.number > 1) {
        jQuery("#menu-button-previous, #board-button-previous").removeClass('disabled').unbind("click").one("click", function(){
          that.goToPreviousPage();
        });
      } else {
        jQuery("#menu-button-previous, #board-button-previous").addClass('disabled').unbind("click");
      }
      if (that.currentPage.number < that.documentData.numberOfPages) {
        jQuery("#menu-button-next, #board-button-next").removeClass('disabled').unbind("click").one("click", function(){
          that.goToNextPage();
        });
      } else {
        jQuery("#menu-button-next, #board-button-next").addClass('disabled').unbind("click");
      }
    }
  }
  this.navigation.init();  
    
  this.slider = {
    init: function() {
      // Constructs the slider
      var sliderPage = jQuery("#thumbnails-slider>div:first").clone();
      var sliderPageWidth = (100/that.documentData.numberOfPages) + "%";
      jQuery("#thumbnails-slider>div:first").remove();
      for(var i=1; i<=that.documentData.numberOfPages; i++){
        var newSliderPage = sliderPage.clone();
        newSliderPage.css({ width:sliderPageWidth })
          .attr("title", "page " + i)
          .click(function(){
            that.goToPage(jQuery(this).index()+1);
          });
        if (i===that.documentData.numberOfPages) newSliderPage.addClass("last");
          jQuery("#thumbnails-slider").append(newSliderPage);
      }
    },
    update: function() {
      jQuery("#thumbnails-slider>div").removeClass("current");
      jQuery(jQuery("#thumbnails-slider>div")[that.currentPage.number-1]).addClass("current");
    }
  }
  this.slider.init();  
  
  this.fullscreen = {
    enable: function() {
      jQuery("#menu-button-full").removeClass("disabled").unbind("click").click(function(){
        that.switchToFullMode();
      });      
    },
    disable: function() {
      jQuery("#menu-button-full").addClass("disabled").unbind("click");
    }
  }
  this.fullscreen.enable();
  
  //jQuery("#menu-share-email").click(function(){that.showSharing()});
  
  jQuery("#shareDoc")
   .toggle(
      function(){ jQuery("#menu-share-dropdown").show() },
      function(){ jQuery("#menu-share-dropdown").hide() });
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
  
  

  if(!this.documentData.hasPdf){ 
    jQuery("#menu-export-hasPdf>a").addClass("disabled");
    jQuery("#menu-export-hasPdf").addClass("disabled");
  }else{ 
    jQuery("#menu-export-hasPdf")
      .hover(
        function(){
          jQuery(this).children("a").addClass("over");
          jQuery(this).addClass("over")}, 
        function(){
          jQuery(this).children("a").removeClass("over");
          jQuery(this).removeClass("over");
      })
      .children("a")
        .attr("href", this.documentData.pagesBaseUrl + "/" + this.documentData.uuid + ".pdf")
        .attr("target", "_blank");
  }
  if(!this.documentData.hasUbz){
    jQuery("#menu-export-hasUbz>a").addClass("disabled");
    jQuery("#menu-export-hasUbz").addClass("disabled");
  }else{ 
    jQuery("#menu-export-hasUbz")
      .hover(
        function(){
          jQuery(this).children("a").addClass("over");
          jQuery(this).addClass("over")}, 
        function(){
          jQuery(this).children("a").removeClass("over");
          jQuery(this).removeClass("over");
      })
      .children("a")
        .attr("href", this.documentData.pagesBaseUrl + "/" + this.documentData.uuid + ".ubz")
        .attr("target", "_blank");
  }

  jQuery(document).click(function(){
    if(jQuery("#menu-export-dropdown").css("display") !== "none"){
      jQuery("#menu-button-export").click();
    }
    if(jQuery("#menu-share-dropdown").css("display") !== "none"){
      if(that.state == "embedded"){
        jQuery("#menu-list-share").click();
      }else{
        jQuery("#shareDoc").click();
      }
    }
  });
  jQuery("#menu-share-twitter>a").attr("href", "http://twitter.com/home?status=Currently reading " + this.documentData.pagesBaseUrl);
  jQuery("#description-text").append("<a href='mailto:" + this.documentData.authorEmail + "'>" + this.documentData.author + 
                                      "</a><br/>" + this.documentData.title + 
                                      "<br/>" + this.formatDate(this.documentData.publishedAt) + 
                                      "<br/><br/>" + this.documentData.description);
  jQuery("#menubottom-input").after("/" + this.documentData.numberOfPages);
  
  // disable selection and dragging, has undesired efects on swipe gesture and other functionalities
  jQuery("div").live('selectstart dragstart', function(evt){ evt.preventDefault(); return false; });
  jQuery("img").live('selectstart dragstart', function(evt){ evt.preventDefault(); return false; });
  
  jQuery("#page-img").bind("swipeone", function(event, obj){
    obj.originalEvent.preventDefault();
    console.log("JGESTURES: swipeone");
    if (obj.direction.lastX == 1)
      that.goToPreviousPage()
    else
      that.goToNextPage();
  });

  this.openPage(1);  
};

UbPlayer.Player.prototype.goToNextPage = function() {
  this.goToPage("NEXT");
}

UbPlayer.Player.prototype.goToPreviousPage = function() {
  this.goToPage("PREVIOUS");
}

UbPlayer.Player.prototype.goToPage = function(pageNumber){
  var checkPoint = { finish:"%", start:"%" };
  var that = this;
  
  if (pageNumber === "NEXT" && this.currentPage.number < this.documentData.numberOfPages) {
    this.currentPage.number++;
    checkPoint = { finish:"-200%", start:"100%" };
  } else if(pageNumber === "PREVIOUS" && this.currentPage.number > 1) {
    this.currentPage.number--;
    checkPoint = { finish:"100%", start:"-200%" };
  } else if (typeof pageNumber == "number") {
    if (pageNumber > this.currentPage.number && pageNumber <= this.documentData.numberOfPages) {
      this.currentPage.number = pageNumber;
      checkPoint = { finish:"-200%", start:"100%" };
    } else if (pageNumber < this.currentPage.number && pageNumber >= 1) {
      this.currentPage.number = pageNumber;
      checkPoint = { finish:"100%", start:"-200%" };
    } else {
      return 0;
    }
  }
  
  jQuery("#current-page")
    .unbind("mouseenter")
    .unbind("mouseleave");
  
  this.openPage(this.currentPage.number);
    
  this.viewer.hide();
  /*
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
  */
}

UbPlayer.Player.prototype.adaptPage = function(){
  jQuery(window).resize();
  this.adaptPageTimer = setTimeout(function(){adaptPage()}, 10);
}

UbPlayer.Player.prototype.switchToFullMode = function(){
  var that = this;  
  jQuery(".board-button-unit").show();
  jQuery("#head").show();
  jQuery("#foot").hide();
  that.indexThumbnails.hide();
  jQuery("#quitFullscreen").unbind("click").one("click", function(){
    that.switchToNormalMode();
  });  
  jQuery(window).resize();
  this.mode = "full";
}

UbPlayer.Player.prototype.switchToNormalMode = function(){
  jQuery(".board-button-unit").hide();
  jQuery("#head").hide();
  jQuery("#foot").show();
  jQuery("#quitFullscreen").unbind("click").one("click", function(){
    that.switchToFullMode();
  });
  jQuery(window).resize();
  this.mode = "normal";
}

UbPlayer.Player.prototype.showSharing = function(){
  jQuery("#boards").animate({marginTop:"100%"}, function(){
     jQuery(this).hide();
     jQuery("#sharing").show();
   });
}

UbPlayer.Player.prototype.hideSharing = function(){

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
  //var formattedPageNumber = this.formatPageNumber(pageNumber);
  //var jsonName = this.documentData.pagesBaseUrl + "/page" + formattedPageNumber + ".json";
                       
  this.currentPage.number = pageNumber;

  //jQuery(".appImg").remove();
  //jQuery("#menubottom-input").val(pageNumber);
    
  that.slider.update();
  that.thumbnails.update();
  that.indexThumbnails.update();
  
  jQuery("#current-page>img").attr("src", that.pagesImg[pageNumber].src);
  
  // Slider handler
  if(!this.thumbnails.sliding)
    jQuery("#thumbnails-slider-handler")
      .appendTo(jQuery("#thumbnails-slider>div")[pageNumber-1])
      .html(pageNumber);
  /*  
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
                that.viewer.show(app.src, widget.nominalWidth, widget.nominalHeight);
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
  });*/
  
  jQuery(window).resize();

  that.navigation.update();
  that.thumbnails.update();
}
