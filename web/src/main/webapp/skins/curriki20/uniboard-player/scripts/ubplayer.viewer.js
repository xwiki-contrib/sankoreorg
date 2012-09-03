
UbPlayer.Viewer = function() {
  var that = this;
  
  this.appWidth = 0;
  this.appHeight = 0;
  
  jQuery("#app-viewer").hide();
  jQuery("#app-viewer").click(function(){
    that.hide();
  });
};

UbPlayer.Viewer.prototype.show = function(appUrl, width, height){
  this.appWidth = width;
  this.appHeight = height;
  
  jQuery("#app-viewer-app")
    .attr("src", appUrl)
    .width(this.appWidth)
    .height(this.appHeight);
  jQuery("#app-viewer-appborder")
    .width(this.appWidth + 20)
    .height(this.appHeight + 35);
    
  jQuery("#app-viewer-background").show();
  jQuery("#app-viewer").show();
  jQuery("#app-viewer-app").show();
  jQuery("#app-viewer-background").animate({opacity:"0.8"},600);
  jQuery("#app-viewer").animate({opacity:"1"});

}

UbPlayer.Viewer.prototype.hide = function(){
  jQuery("#app-viewer-background").animate(
      {opacity:"0"}, 
      500, 
      function(){
        jQuery(this).hide()
      });
  jQuery("#app-viewer").animate(
    {opacity:"0"}, 
    function(){
      jQuery(this).hide();
      jQuery("#app-viewer-app")
        .attr("src", "")
        .hide();
    });
}