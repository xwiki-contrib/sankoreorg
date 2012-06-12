/**
 * @class Ext.layout.ToolbarLayout
 * @extends Ext.layout.ContainerLayout
 * Layout manager used by Ext.Toolbar. This is highly specialised for use by Toolbars and would not
 * usually be used by any other class.
 */
Ext.ns('Curriki.layout');
Curriki.layout.ToolbarLayout = Ext.extend(Ext.layout.ContainerLayout, {
    monitorResize : true,

    type: 'xtoolbar',

    /**
     * @property triggerWidth
     * @type Number
     * The width allocated for the menu trigger at the extreme right end of the Toolbar
     */
    triggerWidth: 18,

    /**
     * @property noItemsMenuText
     * @type String
     * HTML fragment to render into the toolbar overflow menu if there are no items to display
     */
    noItemsMenuText : '<div class="x-toolbar-no-items">(None)</div>',

    /**
     * @private
     * @property lastOverflow
     * @type Boolean
     * Used internally to record whether the last layout caused an overflow or not
     */
    lastOverflow: false,

    /**
     * @private
     * @property tableHTML
     * @type String
     * String used to build the HTML injected to support the Toolbar's layout. The align property is
     * injected into this string inside the td.x-toolbar-left element during onLayout.
     */
    tableHTML: [
        '<table cellspacing="0" class="x-toolbar-ct">',
            '<tbody>',
                '<tr>',
                    '<td class="x-toolbar-left" align="left">',                        
                    '</td>',
                    '<td class="x-toolbar-center" align="center">',                        
                    '</td>',
                    '<td class="x-toolbar-right" align="right">',                        
                    '</td>',                    
                '</tr>',
            '</tbody>',
        '</table>'
    ].join(""),

    /**
     * @private
     * Create the wrapping Toolbar HTML and render/move all the items into the correct places
     */
    onLayout : function(ct, target) {
        //render the Toolbar <table> HTML if it's not already present
        if (!this.leftTd) {
            
            var align = 'left';

            target.addClass('x-toolbar-layout-ct');
            target.insertHtml('beforeEnd', String.format(this.tableHTML, align));

            this.leftTd   = target.child('td.x-toolbar-left', true);
            this.centerTd = target.child('td.x-toolbar-center', true);
            this.rightTd  = target.child('td.x-toolbar-right', true);            
            

            if (this.hiddenItem == undefined) {
                /**
                 * @property hiddenItems
                 * @type Array
                 * Holds all items that are currently hidden due to there not being enough space to render them
                 * These items will appear on the expand menu.
                 */
                this.hiddenItems = [];
            }
        

        var side     = 'left';
        var td     = this.leftTd;
        var items    = ct.items.items;
        var position = 0;

        //render each item if not already rendered, place it into the correct (left or right) target
        for (var i = 0, len = items.length, c; i < len; i++, position++) {          
            c = items[i];
            c.initialConfig.hideParent = false; 
            c.hideParent = false;             

            if (c.isFill) {
                if (side == 'left') {
                    td = this.centerTd;
                    side = 'center';
                } else {
                  td = this.rightTd;
                }                    
                position = -1;
            } else if (!c.rendered) {
                c.render(td);
                this.configureItem(c);
            } else {
                if (!c.xtbHidden && !this.isValidParent(c, td)) {                    
                    td.appendChild(c.getPositionEl().dom);
                    c.container = Ext.get(td);
                }
            }            
        }

        //strip extra empty cells
        this.cleanup(this.leftTd);
        this.cleanup(this.centerTd);
        this.cleanup(this.rightTd);        
        this.fitToSize(target);
        }
    },

    /**
     * @private
     * Removes any empty nodes from the given element
     * @param {Ext.Element} el The element to clean up
     */
    cleanup : function(el) {      
        var cn = el.childNodes, i, c;

        for (i = cn.length-1; i >= 0 && (c = cn[i]); i--) {
            if (!c.firstChild) {
                el.removeChild(c);
            }
        }        
    },

    /**
     * @private
     * Hides an item because it will not fit in the available width. The item will be unhidden again
     * if the Toolbar is resized to be large enough to show it
     * @param {Ext.Component} item The item to hide
     */
    hideItem : function(item) {
        /*this.hiddenItems.push(item);

        item.xtbHidden = true;
        item.xtbWidth = item.getPositionEl().dom.parentNode.offsetWidth;
        item.hide();*/
    },

    /**
     * @private
     * Unhides an item that was previously hidden due to there not being enough space left on the Toolbar
     * @param {Ext.Component} item The item to show
     */
    unhideItem : function(item) {
        /*item.show();
        item.xtbHidden = false;
        this.hiddenItems.remove(item);*/
    },

    /**
     * @private
     * Returns the width of the given toolbar item. If the item is currently hidden because there
     * is not enough room to render it, its previous width is returned
     * @param {Ext.Component} c The component to measure
     * @return {Number} The width of the item
     */
    getItemWidth : function(c) {
        return c.hidden ? (c.xtbWidth || 0) : c.getPositionEl().dom.parentNode.offsetWidth;
    },

    /**
     * @private
     * Called at the end of onLayout. At this point the Toolbar has already been resized, so we need
     * to fit the items into the available width. We add up the width required by all of the items in
     * the toolbar - if we don't have enough space we hide the extra items and render the expand menu
     * trigger.
     * @param {Ext.Element} target The Element the Toolbar is currently laid out within
     */
    fitToSize : function(target) {        
    },

    /**
     * @private
     * Returns a menu config for a given component. This config is used to create a menu item
     * to be added to the expander menu
     * @param {Ext.Component} component The component to create the config for
     * @param {Boolean} hideOnClick Passed through to the menu item
     */
    createMenuConfig : function(component, hideOnClick){        
    },

    /**
     * @private
     * Adds the given Toolbar item to the given menu. Buttons inside a buttongroup are added individually.
     * @param {Ext.menu.Menu} menu The menu to add to
     * @param {Ext.Component} component The component to add
     */
    addComponentToMenu : function(menu, component) {        
    },

    /**
     * @private
     * Deletes the sub-menu of each item in the expander menu. Submenus are created for items such as
     * splitbuttons and buttongroups, where the Toolbar item cannot be represented by a single menu item
     */
    clearMenu : function(){        
    },

    /**
     * @private
     * Called before the expand menu is shown, this rebuilds the menu since it was last shown because
     * it is possible that the items hidden due to space limitations on the Toolbar have changed since.
     * @param {Ext.menu.Menu} m The menu
     */
    beforeMoreShow : function(menu) {        
    },

    /**
     * @private
     * Creates the expand trigger and menu, adding them to the <tr> at the extreme right of the
     * Toolbar table
     */
    initMore : function(){       
    },

    destroy : function(){
        Ext.destroy(this.more, this.moreMenu);
        delete this.leftTd;
        delete this.centerTd;
        delete this.rightTd;        
        Curriki.layout.ToolbarLayout.superclass.destroy.call(this);
    }
});

Ext.Container.LAYOUTS.xtoolbar = Curriki.layout.ToolbarLayout;

/**
 * @class Ext.layout.ToolbarLayout
 * @extends Ext.layout.ContainerLayout
 * Layout manager used by Ext.Toolbar. This is highly specialised for use by Toolbars and would not
 * usually be used by any other class.
 */
Curriki.layout.PagingToolbarLayout = Ext.extend(Ext.layout.ContainerLayout, {
    monitorResize : true,

    type: 'xpagingtoolbar',

    /**
     * @property triggerWidth
     * @type Number
     * The width allocated for the menu trigger at the extreme right end of the Toolbar
     */
    triggerWidth: 18,

    /**
     * @property noItemsMenuText
     * @type String
     * HTML fragment to render into the toolbar overflow menu if there are no items to display
     */
    noItemsMenuText : '<div class="x-toolbar-no-items">(None)</div>',

    /**
     * @private
     * @property lastOverflow
     * @type Boolean
     * Used internally to record whether the last layout caused an overflow or not
     */
    lastOverflow: false,

    /**
     * @private
     * @property tableHTML
     * @type String
     * String used to build the HTML injected to support the Toolbar's layout. The align property is
     * injected into this string inside the td.x-toolbar-left element during onLayout.
     */
    tableHTML: [
        '<table cellspacing="0" class="x-toolbar-ct curriki-paging-toolbar">',
            '<tbody>',
                '<tr>',
                    '<td class="x-toolbar-extras" colspan="2" align="center">',
                      '<table cellspacing="0">',
                        '<tbody>',
                          '<tr class="x-toolbar-extras-row"></tr>',
                        '</tbody>',
                      '</table>',                        
                    '</td>',                                       
                '</tr>',
                '<tr>',
                  '<td class="x-toolbar-left" align="{0}">',
                      '<table cellspacing="0">',
                        '<tbody>',
                          '<tr class="x-toolbar-left-row"></tr>',
                        '</tbody>',
                      '</table>',                        
                    '</td>',
                    '<td class="x-toolbar-right" align="right">',
                      '<table cellspacing="0">',
                        '<tbody>',
                          '<tr class="x-toolbar-right-row"></tr>',
                        '</tbody>',
                      '</table>',                        
                    '</td>',                                     
                '</tr>',
            '</tbody>',
        '</table>'
    ].join(""),

    /**
     * @private
     * Create the wrapping Toolbar HTML and render/move all the items into the correct places
     */
    onLayout : function(ct, target) {
        //render the Toolbar <table> HTML if it's not already present
        if (!this.leftTr) {
            
            var align = 'left';

            target.addClass('x-toolbar-layout-ct');
            target.insertHtml('beforeEnd', String.format(this.tableHTML, align));

            this.leftTr   = target.child('tr.x-toolbar-left-row', true);            
            this.rightTr  = target.child('tr.x-toolbar-right-row', true); 
            this.extrasTr = target.child('tr.x-toolbar-extras-row', true);           
            

            if (this.hiddenItem == undefined) {
                /**
                 * @property hiddenItems
                 * @type Array
                 * Holds all items that are currently hidden due to there not being enough space to render them
                 * These items will appear on the expand menu.
                 */
                this.hiddenItems = [];
            }
        
        

        var side     = ct.buttonAlign == 'right' ? this.rightTr : this.leftTr,
            items    = ct.items.items,
            position = 0;

        //render each item if not already rendered, place it into the correct (left or right) target
        for (var i = 0, len = items.length, c; i < len; i++, position++) {          
            c = items[i];
            //c.initialConfig.hideParent = false; 
            //c.hideParent = false;             

            if (c.isFill) {
                if (side == this.leftTr)
                  side = this.extrasTr
                else
                  side = this.rightTr;
                                  
                position = -1;
            } else if (!c.rendered) {
                c.render(this.insertCell(c, side, position));
                this.configureItem(c);
            } else {
                if (!c.xtbHidden && !this.isValidParent(c, side.childNodes[position])) {                
                    var td = this.insertCell(c, side, position);
                    td.appendChild(c.getPositionEl().dom);
                    c.container = Ext.get(td);
                }
            }            
        }

        //strip extra empty cells
        //this.cleanup(this.leftTd);
        this.cleanup(this.leftTr);
        this.cleanup(this.rightTr);
        this.cleanup(this.extrasTr);        
        this.fitToSize(target); 
        }      
    },

    /**
     * @private
     * Removes any empty nodes from the given element
     * @param {Ext.Element} el The element to clean up
     */
    cleanup : function(el) {      
        var cn = el.childNodes, i, c;

        for (i = cn.length-1; i >= 0 && (c = cn[i]); i--) {
            if (!c.firstChild) {
                el.removeChild(c);
            }
        }        
    },
    
    /**
     * @private
     * Inserts the given Toolbar item into the given element
     * @param {Ext.Component} c The component to add
     * @param {Ext.Element} target The target to add the component to
     * @param {Number} position The position to add the component at
     */
    insertCell : function(c, target, position) {
        var td = document.createElement('td');
        td.className = 'x-toolbar-cell';

        target.insertBefore(td, target.childNodes[position] || null);

        return td;
    },

    /**
     * @private
     * Hides an item because it will not fit in the available width. The item will be unhidden again
     * if the Toolbar is resized to be large enough to show it
     * @param {Ext.Component} item The item to hide
     */
    hideItem : function(item) {
        /*this.hiddenItems.push(item);

        item.xtbHidden = true;
        item.xtbWidth = item.getPositionEl().dom.parentNode.offsetWidth;
        item.hide();*/
    },

    /**
     * @private
     * Unhides an item that was previously hidden due to there not being enough space left on the Toolbar
     * @param {Ext.Component} item The item to show
     */
    unhideItem : function(item) {
        /*item.show();
        item.xtbHidden = false;
        this.hiddenItems.remove(item);*/
    },

    /**
     * @private
     * Returns the width of the given toolbar item. If the item is currently hidden because there
     * is not enough room to render it, its previous width is returned
     * @param {Ext.Component} c The component to measure
     * @return {Number} The width of the item
     */
    getItemWidth : function(c) {
        return c.hidden ? (c.xtbWidth || 0) : c.getPositionEl().dom.parentNode.offsetWidth;
    },

    /**
     * @private
     * Called at the end of onLayout. At this point the Toolbar has already been resized, so we need
     * to fit the items into the available width. We add up the width required by all of the items in
     * the toolbar - if we don't have enough space we hide the extra items and render the expand menu
     * trigger.
     * @param {Ext.Element} target The Element the Toolbar is currently laid out within
     */
    fitToSize : function(target) {        
    },

    /**
     * @private
     * Returns a menu config for a given component. This config is used to create a menu item
     * to be added to the expander menu
     * @param {Ext.Component} component The component to create the config for
     * @param {Boolean} hideOnClick Passed through to the menu item
     */
    createMenuConfig : function(component, hideOnClick){        
    },

    /**
     * @private
     * Adds the given Toolbar item to the given menu. Buttons inside a buttongroup are added individually.
     * @param {Ext.menu.Menu} menu The menu to add to
     * @param {Ext.Component} component The component to add
     */
    addComponentToMenu : function(menu, component) {        
    },

    /**
     * @private
     * Deletes the sub-menu of each item in the expander menu. Submenus are created for items such as
     * splitbuttons and buttongroups, where the Toolbar item cannot be represented by a single menu item
     */
    clearMenu : function(){        
    },

    /**
     * @private
     * Called before the expand menu is shown, this rebuilds the menu since it was last shown because
     * it is possible that the items hidden due to space limitations on the Toolbar have changed since.
     * @param {Ext.menu.Menu} m The menu
     */
    beforeMoreShow : function(menu) {        
    },

    /**
     * @private
     * Creates the expand trigger and menu, adding them to the <tr> at the extreme right of the
     * Toolbar table
     */
    initMore : function(){       
    },

    destroy : function(){
        Ext.destroy(this.more, this.moreMenu);
        //delete this.leftTd;
        delete this.centerTd;
       //delete this.rightTd;        
        Curriki.layout.ToolbarLayout.superclass.destroy.call(this);
    }
});

Ext.Container.LAYOUTS.xpagingtoolbar = Curriki.layout.PagingToolbarLayout;