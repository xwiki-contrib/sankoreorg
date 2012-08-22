Ext.ns('Curriki.ui.tree');
/**
 * @class Curriki.ui.tree.TreeNode
 * @extends Ext.tree.TreeNode
 */
Curriki.ui.tree.TreeNode = function(config){   
  this.defaultUI = Curriki.ui.tree.TreeNodeUI;
  Curriki.ui.tree.TreeNode.superclass.constructor.apply(this, arguments);
};
Ext.extend(Curriki.ui.tree.TreeNode, Ext.tree.TreeNode, {  
  delayedCollapse: function(delay) {
    if(!this.collapseProcId & !this.isRoot){
      this.collapseProcId = this.collapse.defer(delay, this);
    }
  },
  cancelCollapse: function() {
    if(this.collapseProcId){
      clearTimeout(this.collapseProcId);
    }
    this.collapseProcId = false;
  },
  appendChild : function(n){
    var node = Ext.tree.TreeNode.superclass.appendChild.call(this, n);
    if (node.parentNode && node.parentNode.childNodes.length > 1) {
      node.previousSibling = node.parentNode.childNodes[node.parentNode.childNodes.length-2];
      node.previousSibling.nextSibling = node;
    }
    return node;
  }
});

/**
 * @class Curriki.ui.tree.AsyncTreeNode
 * @extends Curriki.ui.TreeNode
 */
Curriki.ui.tree.AsyncTreeNode = function(config) {
  this.loaded = config && config.loaded === true;
  this.loading = false;
  
  Curriki.ui.tree.AsyncTreeNode.superclass.constructor.apply(this, arguments);
  
  this.addEvents('beforeload', 'load');
};
Ext.extend(Curriki.ui.tree.AsyncTreeNode, Curriki.ui.tree.TreeNode, {
  expand : function(deep, anim, callback) {
    if(this.loading) { // if an async load is already running, waiting til it's done
      var timer;
      var f = function() {
        if(!this.loading){ // done loading
          clearInterval(timer);
          this.expand(deep, anim, callback);
        }
      }.createDelegate(this);
      timer = setInterval(f, 200);
      return;
    }
    if(!this.loaded) {
      if(this.fireEvent("beforeload", this) === false){
        return;
      }
      this.loading = true;
      this.ui.beforeLoad(this);
      var loader = this.loader || this.attributes.loader || this.getOwnerTree().getLoader();
      if(loader) {
        loader.load(this, this.loadComplete.createDelegate(this, [deep, anim, callback]));
        return;
      }
    }
    Curriki.ui.tree.AsyncTreeNode.superclass.expand.call(this, deep, anim, callback);
  },
    
  /**
   * Returns true if this node is currently loading
   * @return {Boolean}
   */
  isLoading : function() {
    return this.loading;  
  },
    
  loadComplete : function(deep, anim, callback) {
    this.loading = false;
    this.loaded = true;
    this.ui.afterLoad(this);
    this.fireEvent("load", this);
    this.expand(deep, anim, callback);
  },
    
  /**
   * Returns true if this node has been loaded
   * @return {Boolean}
   */
  isLoaded : function() {
    return this.loaded;
  },
    
  hasChildNodes : function() {
    if(!this.isLeaf() && !this.loaded) {
      return true;
    } else {
      return Curriki.ui.tree.AsyncTreeNode.superclass.hasChildNodes.call(this);
    }
  },

  /**
   * Trigger a reload for this node
   * @param {Function} callback
   */
  reload : function(callback) {
    this.collapse(false, false);
    while(this.firstChild) {
      this.removeChild(this.firstChild).destroy();
    }
    this.childrenRendered = false;
    this.loaded = false;
    if(this.isHiddenRoot()){
      this.expanded = false;
    }
    this.expand(false, false, callback);
  }
});

/**
 * @class Curriki.ui.tree.TreeLoader
 * @extends Ext.tree.TreeLoader
 */
Curriki.ui.tree.TreeLoader = function(config) {  
  this.baseParams = {};
  Ext.apply(this, config);
  this.addEvents(
    "beforeload",
    "load",
    "loadexception"
  );
  Curriki.ui.tree.TreeLoader.superclass.constructor.apply(this, arguments);
}
Ext.extend(Curriki.ui.tree.TreeLoader, Ext.tree.TreeLoader, {
  createNode : function(attr) {
    // apply baseAttrs, nice idea Corey!
    if(this.baseAttrs){
      Ext.applyIf(attr, this.baseAttrs);
    }
    if(this.applyLoader !== false){
      attr.loader = this;
    }
    if(typeof attr.uiProvider == 'string'){
      attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
    }
    if(attr.nodeType){
      return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
    } else {
      return attr.leaf ?
        new Curriki.ui.tree.TreeNode(attr) :
        new Curriki.ui.tree.AsyncTreeNode(attr);
    }
  },
  doPreload : function(node) {
    if(node.attributes.children) {
      if(node.childNodes.length < 1) { // preloaded?
        var cs = node.attributes.children;
        node.beginUpdate();
        for(var i = 0, len = cs.length; i < len; i++) {   
          var cf = cs[i].filters;
          if (!cf
            || cf == this.filters 
            || this.filters.indexOf(cf) != -1 
            || (this.filters.some 
              && this.filters.some(function(f){
                if(cf.indexOf(f) != -1) 
                  return true; 
                return false;
                })
              )
            ) {                 
            var cn = node.appendChild(this.createNode(cs[i]));
            if(this.preloadChildren) {
              this.doPreload(cn);
            }
          }
        }
        node.endUpdate();
      }
      return true;
    } else {
      return false;
    }
  }
});


Curriki.ui.tree.TreeNodeUI = function(node) {
  Curriki.ui.tree.TreeNodeUI.superclass.constructor.apply(this, arguments);
}
Ext.extend(Curriki.ui.tree.TreeNodeUI, Ext.tree.TreeNodeUI, {
  onOver : function(e) {
    /*
    Curriki.ui.tree.TreeNodeUI.superclass.onOver.call(this, e);
 
    if (this.node.parentNode) {
      this.node.parentNode.cancelCollapse();
      this.node.parentNode.ui.addClass('x-tree-node-over');
    }
    
    if (this.node.previousSibling)    
      this.node.previousSibling.collapse();
  
    if (this.node.nextSibling)
      this.node.nextSibling.collapse();    

    if (!this.node.leaf) {
      this.node.cancelCollapse();
      this.node.expand();    
    }*/
  },
  onOut : function(e) {
    /*Curriki.ui.tree.TreeNodeUI.superclass.onOut.call(this, e);
  
    if (!this.node.leaf) {
      this.node.delayedCollapse(100);      
    } else {  
      this.node.parentNode.delayedCollapse(50);
      this.node.parentNode.ui.removeClass('x-tree-node-over');  
    }*/
  },
  animExpand : function(callback) {
    this.node.ownerTree.animating = true;
    var ct = Ext.get(this.ctNode);
    ct.stopFx();
    if(!this.node.isExpandable()){
      this.updateExpandIcon();
      this.ctNode.style.display = "";
      Ext.callback(callback);
      return;
    }   
        
    this.animating = true;
    this.updateExpandIcon();        
    ct.fadeIn('l', {
      callback : function() {
        this.animating = false;   
        this.node.ownerTree.animating = false;            
        Ext.callback(callback);               
      },
      scope: this,
      duration: this.node.ownerTree.duration || .25
    });    

    ct.setLeft(this.node.parentNode.ui.ctNode.getWidth());
    /*
    this.node.ownerTree.setWidth(ct.getWidth() + this.node.parentNode.ui.ctNode.getWidth());*/
    var newheight = this.elNode.offsetParent.offsetTop + this.ctNode.offsetHeight;    
    if(newheight > this.node.ownerTree.defaultHeight) {
      this.node.ownerTree.setHeight(newheight);     
      console.log('default height: '+this.node.ownerTree.defaultHeight+' new height: '+newheight);
      this.node.ownerTree.fireEvent('resize', this.node.ownerTree);     
    } else {
    	this.node.ownerTree.setHeight(this.node.ownerTree.defaultHeight); 
    	this.node.ownerTree.fireEvent('resize', this.node.ownerTree);      
    }    
  },  
  animCollapse : function(callback) {
    var ct = Ext.get(this.ctNode);
    ct.enableDisplayMode('block');
    ct.stopFx();    

    this.animating = true;
    this.updateExpandIcon();

    ct.fadeOut('r', {
      callback : function(){
        this.animating = false;      
        Ext.callback(callback);
      },
      scope: this,
      duration: this.node.ownerTree.duration || .25
    });   
    
    if (!this.node.ownerTree.animating) {
      this.node.ownerTree.setHeight(this.node.ownerTree.defaultHeight);
      this.node.ownerTree.fireEvent('resize', this.node.ownerTree);
    }
  },
  toggleCheck : function(value) {
    var cb = this.checkbox;
    if(cb) {
      if(value === undefined) {
        cb.checked = !cb.checked;
      } else {
        cb.checked = value;
      }
      this.onCheckChange();
    }
  },
  expand: function() {
    if (this.node.ownerTree.lastNode && this.node.ownerTree.lastNode != this.node)
      this.node.ownerTree.lastNode.collapse();
    if (this.node.isExpandable() && !this.node.expanded) {
      this.node.expand();
      this.node.ownerTree.lastNode = this.node;
    }
  },
  collapse: function() {
    if (this.node.isExpandable() && this.node.expanded) {
      this.node.collapse();
    }
  },
  toggle: function() {
    if (this.node.isExpandable()) {
      if (this.node.expanded)
        this.collapse();
      else
        this.expand();
    } else {
        // collapse last
        if (this.node.ownerTree.lastNode && this.node.ownerTree.lastNode != this.node && this.node.ownerTree.lastNode != this.node.parentNode)
            this.node.ownerTree.lastNode.collapse();
    }
  },
  onClick : function(e) {
    if(this.dropping) {
      e.stopEvent();
      return;
    }
    if(this.fireEvent("beforeclick", this.node, e) !== false) {
      var a = e.getTarget('a');
      if(!this.disabled && this.node.attributes.href && a){
        this.fireEvent("click", this.node, e);
        return;
      } else if(a && e.ctrlKey) {
        e.stopEvent();
      }
      e.preventDefault();
      if(this.disabled){
        return;
      }
      /*
      if(this.checkbox) {
        this.toggleCheck();
        if(!this.node.leaf) {
          var cb = this.checkbox.checked
          Ext.each(this.node.childNodes, function(item){
            item.ui.toggleCheck(cb);
          });
        }
      }*/

      //if (this.node.attributes.singleClickExpand && !this.animating && this.node.isExpandable()){

      this.toggle();
      //}

      this.fireEvent("click", this.node, e);
    } else {
      e.stopEvent();
    }
  },
  getChildIndent : function() {
    this.childIndent = "";
    return this.childIndent;
  },    
  renderElements : function(n, a, targetNode, bulkRender) {
    // add some indent caching, this helps performance when rendering a large tree
    this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

    var cb = typeof a.checked == 'boolean';

    var href = a.href ? a.href : Ext.isGecko ? "" : "#";
    
    var leafcls = n.leaf ? "x-tree-node-leaf" : "";
    var buf = ['<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-unselectable ', leafcls, ' ', a.cls,'" unselectable="on">',
      cb ? ('<input class="x-tree-node-cb" type="checkbox" ' + (a.checked ? 'checked="checked" />' : '/>')) : '', 
      '<a hidefocus="on" class="x-tree-node-anchor" href="',href,'" tabIndex="1" ',
      a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '><span unselectable="on">',n.text,"</span></a></div>",
      '<ul class="x-tree-node-ct" style="display:none;"></ul>',
      "</li>"].join('');

    var nel;
    if(bulkRender !== true && n.nextSibling && (nel = n.nextSibling.ui.getEl())) {
      this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel, buf);
    } else {
      this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf);
    }
    
    this.elNode = this.wrap.childNodes[0];
    this.ctNode = this.wrap.childNodes[1];
    var index = 0;
    if(cb) {
      this.checkbox = this.elNode.childNodes[0];
      // fix for IE6
      this.checkbox.defaultChecked = this.checkbox.checked;     
      index++;
    }
    this.anchor = this.elNode.childNodes[index];
    this.textNode = this.elNode.childNodes[index].firstChild;
    var ctWidth = Ext.get(this.textNode).getTextWidth() + this.node.ownerTree.offsetWidth;
    if(this.node.parentNode && this.node.parentNode.ui.ctNode.getWidth() < ctWidth) {
      Ext.get(this.node.parentNode.ui.ctNode).setWidth(ctWidth);
    }
  },
    
  updateExpandIcon : function() {},

  onCheckChange : function() {
    var checked = this.checkbox.checked;
    // fix for IE6
    this.checkbox.defaultChecked = checked;
    this.node.attributes.checked = checked;
    if(!this.node.leaf) {
      this.expand();
      if (this.node.isTarget) {
      Ext.each(this.node.childNodes, function(item){
        item.ui.toggleCheck(checked);
      });
      }
    } else {
      if (!this.node.parentNode.ui.isChecked() && checked) {
        this.node.parentNode.isTarget = false;
        this.node.parentNode.ui.toggleCheck(checked);
        this.node.parentNode.isTarget = true;
      }
    }
    this.fireEvent('checkchange', this.node, checked);
  }
});

Curriki.ui.tree.RootTreeNodeUI = Ext.extend(Curriki.ui.tree.TreeNodeUI, {
    // private
    /*render : function(){
        if(!this.rendered){
            var targetNode = this.node.ownerTree.innerCt.dom;
            this.node.expanded = true;
            //targetNode.innerHTML = '<div class="x-tree-root-node" style="width:50px;"></div>';
            //this.wrap = this.ctNode = targetNode.firstChild;
        }
    }
    ,*/collapse : Ext.emptyFn,
    expand : Ext.emptyFn
});

Curriki.ui.tree.TreePanel = Ext.extend(Ext.tree.TreePanel, {
  initComponent: function(){
    this.lastChecked = [];
    this.defaultHeight = 0;
    this.offsetWidth = 50;
    this.animating = false;
    Curriki.ui.tree.TreePanel.superclass.initComponent.call(this);
   
  }
  /*,setRootNode : function(node){
    if(!node.render){ // attributes passed
      node = this.loader.createNode(node);
    }
    this.root = node;
    node.ownerTree = this;
    node.isRoot = true;
    this.registerNode(node);
    if(!this.rootVisible){
      var uiP = node.attributes.uiProvider;
      node.ui = uiP ? new uiP(node) : new Curriki.ui.tree.RootTreeNodeUI(node); 
    }
    return node;
  }*/
  ,afterRender: function() {
    Ext.tree.TreePanel.superclass.afterRender.call(this);
    this.root.render();
    if(!this.rootVisible){
      this.root.renderChildren();
    }
    this.defaultHeight = this.body.getHeight();
    /*var tree = this;
    this.body.on("mouseleave", function(event) {
      if(!event.within(tree.body, true)) {    
        if(tree.lastChecked && tree.lastChecked.length > 0) {      
          tree.lastChecked[tree.lastChecked.length-1].parentNode.cancelCollapse();   
          tree.lastChecked[tree.lastChecked.length-1].parentNode.expand();      
        }
      }
    });
    this.body.on("mouseenter", function(event) {  
      if(tree.lastChecked && tree.lastChecked.length > 0) {    
        tree.lastChecked[tree.lastChecked.length-1].parentNode.collapse();      
      }
    });*/
  }
});
Ext.reg('curriki-treepanel', Curriki.ui.tree.TreePanel);

Curriki.ui.tree.TreePanel.nodeTypes = {};
Curriki.ui.tree.TreePanel.nodeTypes.node = Curriki.ui.tree.TreeNode;
Curriki.ui.tree.TreePanel.nodeTypes.async = Curriki.ui.tree.AsyncTreeNode;