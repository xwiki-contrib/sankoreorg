Ext.ns('Curriki.module.search');
(function(){
	
  var Search = Curriki.module.search;

  Search.settings = {
    gridWidth:(Ext.isIE6?620:'auto')
  };

  Search.stateProvider = new Ext.state.CookieProvider({
  });
  Ext.state.Manager.setProvider(Search.stateProvider);

  Search.sessionProvider = new Ext.state.CookieProvider({
    expires: null // Valid until end of browser session
  });
})();

Ext.ns('Curriki.module.search.util');
(function(){

var Search = Curriki.module.search;
var module = Search.util;

module.init = function(){
  console.log('search util: init');

  module.logFilterList = {
    'resource':['subject', 'level', 'language', 'ict', 'review', 'special', 'other', 'sort', 'dir']
    ,'external':['subject', 'level', 'language', 'ict', 'review', 'special', 'other', 'sort', 'dir']
    ,'group':['subject', 'level', 'language', 'policy', 'other', 'sort', 'dir']
    ,'member':['subject', 'member_type', 'country', 'other', 'sort', 'dir']
    ,'blog':['other', 'sort', 'dir']
    ,'curriki':['other', 'sort', 'dir']
  };

  // Register a listener that will update counts on the tab
  module.registerTabTitleListener = function(modName){
    // Adjust title with count
    Ext.StoreMgr.lookup('search-store-'+modName).addListener(
      'datachanged'
      ,function(store) {
        var overmax = false;
        var totalCount = 0;
        var resultCount = store.getTotalCount();
        if (!Ext.isEmpty(store.reader.jsonData) && !Ext.isEmpty(store.reader.jsonData.totalResults)) {
          totalCount = parseInt(store.reader.jsonData.totalResults);
        }
        if (totalCount > resultCount) {
          overmax = true;
        }

        var tab = Ext.getCmp('search-results-'+modName);
        if (!Ext.isEmpty(tab)) {
          var titleMsg = __('search.tab.title.results');
          if (overmax && (__('search.tab.title.resultsmax_exceeds') !== 'search.tab.title.resultsmax_exceeds')) {
            titleMsg = __('search.tab.title.resultsmax_exceeds');
          }

          tab.setTitle(String.format(titleMsg, __('search.'+modName+'.tab.title'), resultCount, totalCount));

        }

        var pager = Ext.getCmp('search-pager-'+modName);
        if (!Ext.isEmpty(pager)) {
          var afterPageText = __('search.pagination.afterpage');
          if (overmax && (__('search.pagination.afterpage_resultsmax_exceeds') !== 'search.pagination.afterpage_resultsmax_exceeds')) {
            afterPageText = __('search.pagination.afterpage_resultsmax_exceeds');
          }
          pager.afterPageText = String.format(afterPageText, '{0}', totalCount);

          var displayMsg = __('search.pagination.displaying.'+modName);
          if (overmax && (__('search.pagination.displaying.'+modName+'_resultsmax_exceeds') !== 'search.pagination.displaying.'+modName+'_resultsmax_exceeds')) {
            displayMsg = __('search.pagination.displaying.'+modName+'_resultsmax_exceeds');
          }
          pager.displayMsg = String.format(displayMsg, '{0}', '{1}', '{2}', totalCount);
        }
      }
    );

    Ext.StoreMgr.lookup('search-store-'+modName).addListener(
      'load'
      ,function(store, data, options) {
        /*var params = options.params||{};
        var tab = params.module;
        var terms = escape(params.terms||'');
        var advancedPanel = Ext.getCmp('search-advanced-'+tab);
        var advanced = (advancedPanel&&!advancedPanel.collapsed)
                       ?'advanced'
                       :'simple';
        var page = ''; // Only if not first page
        if (params.start) {
          if (params.start !== '0') {
            page = '/start/'+params.start;
          }
        }
        var filters = ''; // Need to construct
        Ext.each(
          module.logFilterList[tab]
          ,function(filter){
            if (!Ext.isEmpty(params[filter], false)){
              filters += '/'+filter+'/'+escape(params[filter]);
            }
          }
        );

        Curriki.logView('/features/search/'+tab+'/'+terms+'/'+advanced+filters+page);

        // Add to history
        Search.doSearch(tab, false, true);*/
      }
    );

  };
	
	module.getFilters = function(modName) {
		
		var filters = {};		

    // Module panel
    filterPanel = Ext.getCmp('search-filterPanel-'+modName);
    if (!Ext.isEmpty(filterPanel)) {
      var filterForm = filterPanel.getForm();
      if (!Ext.isEmpty(filterForm)) {
        Ext.apply(filters, filterForm.getValues(false));
      }
    }
    
    // set terms
    filters.terms = module.getTerms();
		
		return filters;
	}
	
	module.getTerms = function() {
	  
	  var terms = "";
	  
	  // Global panel (if exists)
    var termPanel = Ext.getCmp('search-termPanel');
    if (!Ext.isEmpty(termPanel)) {
      var termForm = termPanel.getForm();
      if (!Ext.isEmpty(termForm)) {
        terms = termForm.getValues(false).terms;
        if (terms && terms === _('search.text.entry.label')) {
          terms = '';
        }
      }
    }
    
    return terms;
	}	
	
	module.applyFiltersFor = function(filterValues, modName) {
		
		var filters = {};
		var list = Search.data[modName].filter.list;
		Ext.each(list, function(filter){
			if (!Ext.isEmpty(filterValues[filter]))
			  filters[filter] = filterValues[filter];
		  else
			  filters[filter] = '';
		});
		return filters;
	}
	
	module.applyTermsFor = function(filters, terms, modName) {	  
	  filters['all'] = terms;
	  
	  return filters;
	}
	
	module.registerStoreListeners = function(modName) {
		
		// Adjust title with count
    Ext.StoreMgr.lookup('search-store-'+modName).addListener(
      'datachanged'
      ,function(store) {
        var overmax = false;
        var totalCount = 0;
        var resultCount = store.getTotalCount();
        if (!Ext.isEmpty(store.reader.jsonData) && !Ext.isEmpty(store.reader.jsonData.totalResults)) {
          totalCount = parseInt(store.reader.jsonData.totalResults);
        }
        if (totalCount > resultCount) {
          overmax = true;
        }

        var grid = Ext.getCmp('search-results-'+modName);
        if (!Ext.isEmpty(grid)) {
			    var titleMsg = __('search.grid.title.results');
			    if (overmax && (__('search.grid.title.resultsmax_exceeds') !== 'search.grid.title.resultsmax_exceeds')) {
				    titleMsg = __('search.grid.title.resultsmax_exceeds');
			    }
			
			    grid.setTitle(String.format(titleMsg, _('search.' + modName + '.grid.title'), resultCount, totalCount));
			
			
			    var pager = Ext.getCmp('search-pager-' + modName);
			    if (!Ext.isEmpty(pager)) {
				    var afterPageText = __('search.pagination.afterpage');
				    if (overmax && (__('search.pagination.afterpage_resultsmax_exceeds') !== 'search.pagination.afterpage_resultsmax_exceeds')) {
					    afterPageText = __('search.pagination.afterpage_resultsmax_exceeds');
				    }
				    pager.afterPageText = String.format(afterPageText, '{0}', totalCount);
				
				    var displayMsg = __('search.pagination.displaying.' + modName);
				    if (overmax && (__('search.pagination.displaying.' + modName + '_resultsmax_exceeds') !== 'search.pagination.displaying.' + modName + '_resultsmax_exceeds')) {
					    displayMsg = __('search.pagination.displaying.' + modName + '_resultsmax_exceeds');
				    }
				    pager.displayMsg = String.format(displayMsg, '{0}', '{1}', '{2}', totalCount);
			    } else {
						if (resultCount === 0)
				      grid.hide();
			    }
		    }
      }
    );
	}

  // Perform a search for a module
  module.doSearch = function(modName, start){
    console.log('Doing search', modName, start);
		
    var filters = module.getFilters(modName);

    console.log('Applying search filters', filters);
		var store = Ext.StoreMgr.lookup('search-store-'+modName);
		if (!Ext.isEmpty(store)) {
			// apply filters
			Ext.apply(Ext.StoreMgr.lookup('search-store-'+modName).baseParams || {}, filters);
			
			console.log('Done util.doSearch', filters);
			// load store
			var pager = Ext.getCmp('search-pager-'+modName)
			console.log('Searching', filters);
      if (!Ext.isEmpty(pager)) {    
        //pager.doLoad(Ext.num(start, 0)); // Reset to first page if the tab is shown
				store.load();
      } else {
        store.load();
      }
			
			var token = {
				's': modName
				,'f': {
				}
				//,'p': {
				//	'c': 0
				//	,'s': 25
				//}
				,'t': modName
				,'a': false
			};
			token['f'][modName] = filters;

      var provider = new Ext.state.Provider();
      var encodedToken = provider.encodeValue(token);
      console.log('Saving History', {values: token});
      Search.history.addToken(encodedToken);
		}    
  };
	
	module.doGridSearch = function(grid, filters) {
		if (!Ext.isEmpty(grid)) {
		  var store = grid.getStore();
		  if (!Ext.isEmpty(store)) {
				Ext.apply(store.baseParams || {}, filters);
				store.load();			
		  }
	  }
	}

  // General term panel (terms and search button)
  module.createTermPanel = function(modName, form) {
    return {
      xtype:'form'      
      ,labelAlign:'left'
      ,id:'search-termPanel'
      ,formId:'search-termForm'
      ,renderTo:'search-term'
      ,cls:'term-panel'
      ,border:false
      ,items:[{
        layout:'column'
        ,border:false
        ,defaults:{border:false}
        ,items:[{
          id:'search-term-icon'
          ,items:[{
            xtype:'box'
            ,autoEl: {
              tag:'img'
              ,src: '/xwiki/bin/skin/curriki20/search_grey.png'
            }
          }]
        },{
          layout:'form'
          ,id:'search-termPanel-form'
          ,cls:'search-termPanel-form'
          ,items:[{
            xtype:'textfield'
            ,id:'search-termPanel-terms'
            ,cls:'search-termPanel-terms'
            ,fieldLabel:_('search.text.entry.label')
            ,name:'terms'
            ,hideLabel:true
            ,emptyText:_('search.text.entry.label')
            ,listeners:{
              specialkey:{
                fn:function(field, e){
                  if (e.getKey() === Ext.EventObject.ENTER) {
                    e.stopEvent();
                    Search.doSearch(modName, true);
                  }
                }
              }
            }
          },{
            xtype:'box'
            ,autoEl:{
              tag:'p'
              ,cls:'left'
              ,html:'Exemples: Pythagore, Géographie, Mathématiques, Histoire, ...'
            }
          },{
            xtype:'container'
            ,autoEl:'p'
            ,cls:'right'
            ,width:300
            ,items:[{
              xtype:'box'
              ,autoEl:{
                tag:'a'
                ,href:'#'
                ,html:'Comment fonctionne le moteur sankoré?'
              }
            }]            
          }]
        },{
          layout:'form'
          ,id:'search-termPanel-buttonColumn'
          ,cls:'search-termPanel-buttonColumn'
          ,items:[{
            xtype:'button'
            ,id:'search-termPanel-button'
            //,cls:'btn btn-large'
            ,text:_('search.text.entry.button')
            ,listeners:{
              click:{
                fn: function(){
                  if (modName == 'global') {
                    terms = Ext.getCmp("search-termPanel-terms").getValue(); 
                    window.location.href = '/xwiki/bin/view/Search/WebHome#o%3As%3Ds%253Aresource%5Ef%3Do%253Aresource%253Do%25253Acategory%25253Ds%2525253A%25255Esystem%25253Ds%2525253AAssetMetadata.InternationalEducation%25255Elevel%25253Ds%2525253A%25255Esublevel%25253Ds%2525253A%25255Esubject%25253Ds%2525253A%25255Esubsubject%25253Ds%2525253A%25255Eict%25253Ds%2525253A%25255Esubict%25253Ds%2525253A%25255Elanguage%25253Ds%2525253A%25255Especial%25253Ds%2525253A%25255Eterms%25253Ds%2525253A'+terms+'%5Et%3Ds%253Aresource%5Ea%3Db%253A0';
                  } else{
                    Search.doSearch(modName, true);
                  }
                }
              }
            }
          }]
        }]
      },{
        xtype:'hidden'
        ,name:'other'
        ,id:'search-termPanel-other'
        ,value:(!Ext.isEmpty(Search.restrictions)?Search.restrictions:'')
      }]
    };
  };

/*
  // General help panel
  module.createHelpPanel = function(modName, form){
    var cookie = 'search_help_'+modName;
    return {
      xtype:'fieldset'
      ,id:'search-helpPanel-'+modName
      ,title:_('search.text.entry.help.button')
      ,collapsible:true
      ,collapsed:((Search.sessionProvider.get(cookie, 0)===0)?true:false)
      ,listeners:{
        collapse:{
          fn:function(panel){
            Search.sessionProvider.clear(cookie);
          }
        }
        ,expand:{
          fn:function(panel){
            Search.sessionProvider.set(cookie, 1);
          }
        }
      }
      ,border:true
      ,autoHeight:true
      ,items:[{
        xtype:'box'
        ,autoEl:{
          tag:'div'
          ,html:_('search.text.entry.help.text')
          ,cls:'help-text'
        }
      }]
    };
  };
*/

  module.fieldsetPanelSave = function(panel, state){
    if (Ext.isEmpty(state)) {
      state = {};
    }
    if (!panel.collapsed) {
      state.collapsed = panel.collapsed;
    } else {
      state = null;
    }
    console.log('fieldset Panel Save state:', state);
    Search.sessionProvider.set(panel.stateId || panel.id, state);
  };

  module.fieldsetPanelRestore = function(panel, state){
    if (!Ext.isEmpty(state)
        && !Ext.isEmpty(state.collapsed)
        && !state.collapsed) {
      panel.expand(false);
    }
  };

  module.registerSearchLogging = function(tab){
  };

};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    module.init();
  });
});
})();

Ext.ns('Curriki.module.search.data.resource');
(function(){
var modName = 'resource';

var data = Curriki.module.search.data.resource;

data.init = function(){
  console.log('data.'+modName+': init');

  // Set up filters
  data.filter = {};
  var f = data.filter; // Alias
  
	f.list = ['terms', 'system', 'level', 'sublevel', 'subject', 'subsubject', 'ict', 'subict', 'category', 'language', 'review', 'special'];

  f.data = {};	
  
  f.data.system = {
    list: Curriki.data.education_system.list
    ,data: [
      ['AssetMetadata.InternationalEducation', _('CurrikiCode.AssetClass_education_system_AssetMetadata.InternationalEducation')]
    ]
  }  
  f.data.system.list.each(function(item){
    f.data.system.data.push([
      item.id
      ,_('CurrikiCode.AssetClass_education_system_'+item.id)
    ]);
  });

  f.data.level =  {
    mapping: Curriki.data.el.elMap['TREEROOTNODE']
    ,list: []
    ,toplist: []
    ,data: [
      //['', _('CurrikiCode.AssetClass_educational_level_UNSPECIFIED'),'']
    ]
  };
  f.data.level.mapping.each(function(value){
    f.data.level.list.push(value.id);
    if(value.parent == '')
      f.data.level.toplist.push(value.id);
  });
  f.data.system.list.each(function(value){
    f.data.level.data.push([
      ''
      ,_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
      ,value.id
    ]);
  });

  f.data.level.list.each(function(value){
    var parent = '';
    f.data.level.mapping.each(function(item){
      if(value == item.id) {
        if(item.parent == '')
          parent = item.value
        else
          parent = item.parent;
      }
    });
      
    f.data.level.data.push([
      value
      ,_('CurrikiCode.AssetClass_educational_level_'+value)
      ,parent
    ]);
  });
  
  
  f.data.sublevel =  {
    mapping: Curriki.data.el.elMap
    ,data: [
    ]
  };
  f.data.level.mapping.each(function(parentItem){
    f.data.sublevel.data.push([
      parentItem.id
      ,_('CurrikiCode.AssetClass_educational_level_'+parentItem.id+'.UNSPECIFIED')
      ,parentItem.id
    ]);
    if(f.data.sublevel.mapping[parentItem.id]) {
      f.data.sublevel.mapping[parentItem.id].each(function(el){
        f.data.sublevel.data.push([
          el.id
          ,_('CurrikiCode.AssetClass_educational_level_'+el.id)
          ,parentItem.id
        ]);
      });
    }
  });
  
  f.data.subject =  {
    mapping: Curriki.data.fw_item.fwMap['TREEROOTNODE']
    ,list: []
    ,data: [
      ['', Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')),'-','-']
    ]
  };
  f.data.level.toplist.each(function(value){
    f.data.subject.data.push([
      ''
      ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED'))
      ,''
      ,value
    ]);
  });
  f.data.subject.mapping.each(function(value){
    f.data.subject.list.push(value.id);
  });

  f.data.subject.list.each(function(value){
    var parent = '-';
    var level = '';
    f.data.subject.mapping.each(function(item){
      if(value == item.id) {
        //parent = item.parent;
        level = item.value;
      }      
    });
    f.data.subject.data.push([
      value
      ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_'+value))
      ,parent
      ,level
    ]);
  });

  f.data.subsubject =  {
    mapping: Curriki.data.fw_item.fwMap
    ,data: [
    ]
  };
  f.data.subject.mapping.each(function(parentItem){
    f.data.subsubject.data.push([
      parentItem.id
      ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_'+parentItem.id+'.UNSPECIFIED'))
      ,parentItem.id
      ,parentItem.value
    ]);
    f.data.subsubject.mapping[parentItem.id].each(function(subject){
      f.data.subsubject.data.push([
        subject.id
        ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_'+subject.id))
        ,parentItem.id
        ,subject.value
      ]);
    });
  });

  f.data.ict =  {
    mapping: Curriki.data.ict.ictMap['TREEROOTNODE']
    ,list: []
    ,data: [
      ['', _('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')]
    ]
  };
  f.data.ict.mapping.each(function(value){
    f.data.ict.list.push(value.id);
  });

  f.data.ict.list.each(function(value){
    f.data.ict.data.push([
      value
      ,_('CurrikiCode.AssetClass_instructional_component_'+value)
    ]);
  });

  f.data.subict =  {
    mapping: Curriki.data.ict.ictMap
    ,data: [
    ]
  };
  f.data.ict.mapping.each(function(parentItem){
    f.data.subict.data.push([
      parentItem.id
      ,_('CurrikiCode.AssetClass_instructional_component_'+parentItem.id+'.UNSPECIFIED')
      ,parentItem.id
    ]);
    if(f.data.subict.mapping[parentItem.id]) {
      f.data.subict.mapping[parentItem.id].each(function(ict){
        f.data.subict.data.push([
          ict.id
          ,_('CurrikiCode.AssetClass_instructional_component_'+ict.id)
          ,parentItem.id
        ]);
      });
    }
  });
  
  f.data.category =  {
    list: Curriki.data.category.list
    ,data: [
      ['', _('CurrikiCode.AssetClass_category_UNSPECIFIED'), ' ']
    ]
  };
  
  f.data.category.list.each(function(value){
    var sort = _('CurrikiCode.AssetClass_category_'+value);
    if (value === 'unknown') {
      sort = 'zzz';
    }    
    f.data.category.data.push([
      value
      ,_('CurrikiCode.AssetClass_category_'+value)
      ,sort
    ]);    
  });
  // category doesn't need to be sorted because it is sorted somewhere further

  f.data.language =  {
    list: Curriki.data.language.list
    ,data: [
      ['', _('CurrikiCode.AssetClass_language_UNSPECIFIED')]
    ]
  };
  f.data.language.list.each(function(value){
    f.data.language.data.push([
      value
      ,_('CurrikiCode.AssetClass_language_'+value)
    ]);
  });

  f.data.review = {
    list: [
      'partners', 'curriki', 'members', 'not_reviewed'
    ]
    ,data: [
      ['', _('search.resource.review.selector.UNSPECIFIED')]
    ]
  };
  f.data.review.list.each(function(review){
    f.data.review.data.push([
      review
      ,_('search.resource.review.selector.'+review)
    ]);
  });

  f.data.special = {
    list: [
      'contributions', 'updated'
    ]
    ,data: [
      ['', _('search.resource.special.selector.UNSPECIFIED')]
    ]
  };
  f.data.special.list.each(function(special){
    f.data.special.data.push([
      special
      ,_('search.resource.special.selector.'+special)
    ]);
  });

  f.store = {
    system: new Ext.data.SimpleStore({
      fields: ['id', 'education_system']
      ,data: f.data.system.data
      ,id: 0
    })
    ,subject: new Ext.data.SimpleStore({
      fields: ['id', 'subject', 'parentItem', 'level']
      ,data: f.data.subject.data
      ,id: 0
    })

    ,subsubject: new Ext.data.SimpleStore({
      fields: ['id', 'subject', 'parentItem', 'level']
      ,data: f.data.subsubject.data
      ,id: 0
    })

    ,level: new Ext.data.SimpleStore({
      fields: ['id', 'level', 'parentItem']
      ,data: f.data.level.data
      ,id: 0
    })

    ,sublevel: new Ext.data.SimpleStore({
      fields: ['id', 'level', 'parentItem']
      ,data: f.data.sublevel.data
      ,id: 0
    })

    ,ict: new Ext.data.SimpleStore({
      fields: ['id', 'ict']
      ,data: f.data.ict.data
      ,id: 0
    })

    ,subict: new Ext.data.SimpleStore({
      fields: ['id', 'ict', 'parentItem']
      ,data: f.data.subict.data
      ,id: 0
    })
    
    ,category: new Ext.data.SimpleStore({
      fields: ['id', 'category', 'sortValue']
      ,sortInfo: {field:'sortValue', direction:'ASC'}
      ,data: f.data.category.data
      ,id: 0
    })

    ,language: new Ext.data.SimpleStore({
      fields: ['id', 'language']
      ,data: f.data.language.data
      ,id: 0
    })

    ,review: new Ext.data.SimpleStore({
      fields: ['id', 'review']
      ,data: f.data.review.data
      ,id: 0
    })

    ,special: new Ext.data.SimpleStore({
      fields: ['id', 'special']
      ,data: f.data.special.data
      ,id: 0
    })
  };

  // Set up data store
  data.store = {};

  data.store.record = new Ext.data.Record.create([
    { name: 'title' }
    ,{ name: 'assetType' }
    ,{ name: 'category' }
    ,{ name: 'subcategory' }
    ,{ name: 'contributor' }
    ,{ name: 'contributorName' }
    ,{ name: 'rating', mapping: 'review' }
    ,{ name: 'memberRating', mapping: 'rating' }
    ,{ name: 'ratingCount' }
    ,{ name: 'description' }
    ,{ name: 'fwItems' }
    ,{ name: 'levels' }
    ,{ name: 'parents' }
    ,{ name: 'updated' }
  ]);

  data.store.results = new Ext.data.Store({
    storeId: 'search-store-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/Resources'
      ,method:'GET'
    })
    ,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  

  // Set up renderers
  data.renderer = {
    title: function(value, metadata, record, rowIndex, colIndex, store){
      // Title
      var page = record.id.replace(/\./, '/');

      var desc = Ext.util.Format.stripTags(record.data.description);
      desc = Ext.util.Format.ellipsis(desc, 256);
      desc = Ext.util.Format.htmlEncode(desc);

      var fw = Curriki.data.fw_item.getRolloverDisplay(record.data.fwItems||[]);
      var lvl = Curriki.data.el.getRolloverDisplay(record.data.levels||[]);

      desc = String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}"
        ,desc,_('global.title.popup.description')
        ,fw,_('global.title.popup.subject')
        ,lvl,_('global.title.popup.educationlevel')
      );

      // Asset Type icon
      var assetType = record.data.assetType;
      var category = record.data.category;
      var subcategory = record.data.subcategory;
      metadata.css = String.format('resource-{0} category-{1} subcategory-{1}_{2}', assetType, category, subcategory); // Added to <td>

      var rollover = _(category+'.'+subcategory);
      if (rollover === category+'.'+subcategory) {
        rollover = _('unknown.unknown');
      }

      return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a href="/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>', page, Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);
    }

    ,contributor: function(value, metadata, record, rowIndex, colIndex, store){
      var page = value.replace(/\./, '/');
      return String.format('<a href="/xwiki/bin/view/{0}">{1}</a>', page, record.data.contributorName);
    }

    ,rating: function(value, metadata, record, rowIndex, colIndex, store){
      if (value != "" && value != 100) {
        var page = record.id.replace(/\./, '/');

        metadata.css = String.format('crs-{0}', value); // Added to <td>
        //metadata.attr = String.format('title="{0}"', _('curriki.crs.rating'+value)); // Added to <div> around the returned HTML
        return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>', value, _('search.resource.review.'+value), Ext.BLANK_IMAGE_URL, page);
      } else {
        return String.format('');
      }
    }

    ,memberRating: function(value, metadata, record, rowIndex, colIndex, store){
      if (value != "" && value != 0) {
        var page = record.id.replace(/\./, '/');
        var ratingCount = record.data.ratingCount;

        metadata.css = String.format('rating-{0}', value);
        return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}"> ({1})</a>', value, ratingCount, page, _('search.resource.rating.'+value), Ext.BLANK_IMAGE_URL);
      } else {
        return String.format('');
      }
    }

    ,updated: function(value, metadata, record, rowIndex, colIndex, store){
      var dt = Ext.util.Format.date(value, 'M-d-Y');
      return String.format('{0}', dt);
    }
    ,result: function(value, metadata, record, rowIndex, colIndex, store) {
      var page = record.id.replace(/\./, '/');

      // BEGIN SKE-568 Embed mode
	  if (embedLinksQueryString) {
	      target = 'target="_blank"';
	  } else {
	      target = '';
	  }
	  // END SKE-568 Embed mode

      var title = String.format('<a href="/xwiki/bin/view/{0}" ' + target + ' >{1}</a>', page, Ext.util.Format.ellipsis(record.data.title, 160));
      var desc = Ext.htmlDecode(Ext.util.Format.ellipsis(Ext.util.Format.stripTags(record.data.description), 256));
      var imgsrc = "/xwiki/skins/curriki20/icons/mediatype/";
      if (record.data.category == "text" || record.data.category == "document") {
        imgsrc = imgsrc + "document_large.gif";
      } else if (record.data.category == "image") {
        imgsrc = imgsrc + "image_large.gif";
      } else if (record.data.category == "audio") {
        imgsrc = imgsrc + "audio_large.gif";
      } else if (record.data.category == "video") {
        imgsrc = imgsrc + "video_large.gif";
      } else if (record.data.category == "interactive") {
        imgsrc = imgsrc + "interactive_large.gif";
      } else if (record.data.category == "archive") {
        imgsrc = imgsrc + "archive_large.gif";
      } else if (record.data.category == "external") {
        imgsrc = imgsrc + "weblink_large.gif";
      } else if (record.data.category == "collection") {
        imgsrc = imgsrc + "collection_large.gif"; 
      } else if (record.data.category == "sankore") {
        imgsrc = imgsrc + "sankore_large.png";      
      } else {
        imgsrc = imgsrc + "archive_large.gif";
      }
      var link = String.format('<a class="preview" href="/xwiki/bin/view/{0}" ' + target + ' ><img src="{1}" /></a>', page, imgsrc);
      
      var memberRating = record.data.memberRating;
      if (memberRating == "")
        memberRating = "0";
      var ratingCount = record.data.ratingCount;
      if (ratingCount == "")
        ratingCount = "0";        
      var rating =  String.format('<span class="rating rating-{0}"><a href="/xwiki/bin/view/{2}?viewer=comments" qtip="{3}" ' + target + ' >{1} avis </a><a href="/xwiki/bin/view/{2}?viewer=comments" ' + target + ' ><img class="rating-icon" src="{4}" qtip="{3}" /></a></span>', memberRating, ratingCount, page, _('search.resource.rating.'+memberRating), Ext.BLANK_IMAGE_URL);
      
      var review = String.format('');
      if (record.data.rating != "") {
        review = String.format('<a class="rating review crs-{0}" title="{1}" href="/xwiki/bin/view/{3}?viewer=comments" ' + target + ' ><span class="crs-text">{1}</span><img class="crs-icon" alt="" src="{2}" /></a>', record.data.rating, _('search.resource.review.'+record.data.rating), Ext.BLANK_IMAGE_URL, page)
      }  
      var cleanContributorName = '/xwiki/bin/view/' +record.data.contributor.trim().replace('.', '/');  
      var contributor = String.format('<a class="contributor" href="{0}">{1}</a>', cleanContributorName, record.data.contributorName);
      
      return String.format('{0}{2}{1}<h4 class="title">{3}</h4>{4}<p class="description">{5}</p>', link, rating, review, title, contributor, desc);
    }
  };
  
  data.store.featuredResults = new Ext.data.Store({
    storeId: 'search-store-featured-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/Resources'
      ,method:'GET'
    })
    ,baseParams: { start: '0', limit: '2', 'featuredResults': '1', xpage: "plain", '_dc':(new Date().getTime()), category:"sankore" }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  data.store.featuredResults.setDefaultSort('updated', 'desc');

  // Set up renderers
  data.featuredRenderer = {
    result: function(value, metadata, record, rowIndex, colIndex, store) {
     var page = record.id.replace(/\./, '/');

     // BEGIN SKE-568 Embed mode
	 if (embedLinksQueryString) {
	     target = 'target="_blank"';
	 } else {
	     target = '';
	 }
	 // END SKE-568 Embed mode

      var title = String.format('<a href="/xwiki/bin/view/{0}" ' + target + ' >{1}</a>', page, Ext.util.Format.ellipsis(record.data.title, 160));
      var desc = Ext.htmlDecode(Ext.util.Format.ellipsis(Ext.util.Format.stripTags(record.data.description), 256));
      var imgsrc = "/xwiki/skins/curriki20/icons/mediatype/";      
      imgsrc = imgsrc + "sankore_large.png";
      var link = String.format('<a class="preview" href="/xwiki/bin/view/{0}" ' + target + ' ><img src="{1}" /></a>', page, imgsrc);
      var rating = String.format('');
      if (record.data.memberRating != "") {
        rating =  String.format('<span class="rating rating-{0}"><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}" ' + target + ' >{1} avis </a><a href="/xwiki/bin/view/{2}?viewer=comments" ' + target + ' ><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a></span>', record.data.memberRating, record.data.ratingCount, page, _('search.resource.rating.'+record.data.memberRating), Ext.BLANK_IMAGE_URL);
      }
      var cleanContributorName = '/xwiki/bin/view/' +record.data.contributor.trim().replace('.', '/'); 
      var contributor = String.format('<a class="contributor" href="{0}" ' + target + ' >{1}</a>', cleanContributorName, record.data.contributorName);
      
      return String.format('{0}{1}<h4 class="title">{2}</h4>{3}<p class="description">{4}</p>', link, rating, title, contributor, desc);
    }
  };
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    data.init();
  });
});
})();

Ext.ns('Curriki.module.search.form.resource');
(function(){
var modName = 'resource';

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
  console.log('form.'+modName+': init');

  var comboWidth = 195;
  var comboListWidth = 250;


  form.termPanel = Search.util.createTermPanel(modName, form);
  //form.helpPanel = Search.util.createHelpPanel(modName, form);
  
  form.filterPanel = {
    xtype: 'form'
    ,labelAlign: 'left'
    ,id: 'search-filterPanel-'+modName
    ,formId: 'search-filterForm-'+modName
    ,border: false    
    ,renderTo:'search-filters'    
    ,items:[{
      xtype:'fieldset'
      ,title:__('search.fieldset-category.label')
      ,id: 'search-advanced-'+modName+'-category'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{
        xtype:'combo'
        ,id:'combo-category-'+modName
        ,fieldLabel:'Category'
        ,hideLabel:true
        ,hiddenName:'category'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.category
        ,displayField:'category'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_category_UNSPECIFIED')
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.combo-system.label')
      ,id: 'search-advanced-'+modName+'-system'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: false
      ,animCollapse: false
      ,border: false
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{
        xtype:'combo'
        ,id:'combo-system-'+modName
        ,fieldLabel:'System'
        ,hideLabel:true
        ,hiddenName:'system'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.system
        ,typeAhead:true
        ,valueField:'id'
        ,displayField:'education_system'               
        ,triggerAction:'all'
        ,value:Curriki.data.education_system.initial        
        ,validator:function(value){
          if(this.store.find('education_system', value) == -1)
            this.setValue(Curriki.data.education_system.initial);
          return true;
        }
        ,listeners:{
          select:{
            fn:function(combo, value){
              var level = Ext.getCmp('combo-level-'+modName);                                      
              level.clearValue();
              var sublevel = Ext.getCmp('combo-sublevel-'+modName);                                  
              sublevel.clearValue();
              sublevel.hide();
              var subject = Ext.getCmp('combo-subject-'+modName);                                        
              subject.clearValue(); 
              var subsubject = Ext.getCmp('combo-subsubject-'+modName);                                        
              subsubject.clearValue();  
              subsubject.hide();                                                      
            }
          }
        }
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.combo-level.label')
      ,id: 'search-advanced-'+modName+'-level'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{            
        xtype:'combo'
        ,id:'combo-level-'+modName
        ,fieldLabel:'Level'
        ,hideLabel:true
        ,hiddenName:'level'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.level
        ,displayField:'level'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
        ,validator:function(value){
          var system = Ext.getCmp('combo-system-'+modName);                
          this.store.filter('parentItem', system.getValue());
          if(this.store.find('level', value) == -1)
            this.clearValue();                                                   
          return true;
        }
        ,listeners:{                 
          expand:{
            fn:function(combo){
              var system = Ext.getCmp('combo-system-'+modName);
              this.store.filter('parentItem', system.getValue());                   
            } 
          }
          ,select:{
            fn:function(combo, value){
              var sublevel = Ext.getCmp('combo-sublevel-'+modName);
              var subject = Ext.getCmp('combo-subject-'+modName);
              var subsubject = Ext.getCmp('combo-subsubject-'+modName);
                  
              if(sublevel.getValue() === '')
                sublevel.setRawValue('UNSPECIFIED');
              sublevel.validate();
              if(subject.getValue() === '')
                subject.setRawValue('UNSPECIFIED');
              subject.validate();
              if(subsubject.getValue() === '')
                subsubject.setRawValue('UNSPECIFIED');
              subsubject.validate();                                       
            }
          }
        }
      },{
        xtype:'combo'
        ,fieldLabel:'Sub Level'
        ,hideLabel:true
        ,id:'combo-sublevel-'+modName
        ,hiddenName:'sublevel'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.sublevel
        ,displayField:'level'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
        ,lastQuery:''
        ,hidden:true
        ,hideMode:'visibility'
        ,validator:function(value){
          var level = Ext.getCmp('combo-level-'+modName);    
          if(level.getValue() === '') {
            this.clearValue();
            this.hide();
          } else {                           
            this.store.filter('parentItem', level.getValue());
            if(this.store.find('level', value) == -1)
              this.clearValue();
            if(this.store.getCount() <= 1) {
              this.clearValue();
              this.hide();
            } else {
              this.show();
            }
          }                
          return true;          
        }
        ,listeners:{
          expand:{
            fn:function(){
              var level = Ext.getCmp('combo-level-'+modName);
              this.store.filter('parentItem', level.getValue());
            }
          }
        }              
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.combo-subject.label')
      ,id: 'search-advanced-'+modName+'-subject'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{       
        xtype:'combo'
        ,id:'combo-subject-'+modName
        ,fieldLabel:'Subject'
        ,hideLabel:true
        ,hiddenName:'subject'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.subject
        ,displayField:'subject'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')
        ,validator:function(value){
          var system = Ext.getCmp('combo-system-'+modName);
          var level = Ext.getCmp('combo-level-'+modName);                                                      
          if(level.getValue())
            this.store.filter('level', level.getValue(), true);
          else
            this.store.filter('parentItem', '-');               
          if(this.store.find('subject', value) == -1)
            this.clearValue();                                        
          return true;
        }
        ,listeners:{
          expand:{
            fn:function(){
              var system = Ext.getCmp('combo-system-'+modName);
              var level = Ext.getCmp('combo-level-'+modName);
              if(level.getValue())
                this.store.filter('level', level.getValue(), true);
              else
                this.store.filter('parentItem', '-');                            
              } 
            }
            ,select:{
              fn:function(combo, value){                                    
                var subsubject = Ext.getCmp('combo-subsubject-'+modName);     
                if(subsubject.getValue() === '')
                  subsubject.setRawValue('UNSPECIFIED');                
                subsubject.validate();                    
              }
            }
          }
        },{
          xtype:'combo'
          ,fieldLabel:'Sub subject'
          ,hideLabel:true
          ,id:'combo-subsubject-'+modName
          ,hiddenName:'subsubject'            
          ,width:comboWidth
          ,listWidth:comboListWidth
          ,mode:'local'
          ,store:data.filter.store.subsubject
          ,displayField:'subject'
          ,valueField:'id'
          ,typeAhead:true
          ,triggerAction:'all'
          //,emptyText:_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')
          ,selectOnFocus:true
          ,forceSelection:true
          ,lastQuery:''
          ,hidden:true
          ,hideMode:'visibility'
          ,validator:function(value){
            var system = Ext.getCmp('combo-system-'+modName);
            var level = Ext.getCmp('combo-level-'+modName);
            var subject = Ext.getCmp('combo-subject-'+modName);
                
            if(subject.getValue() === '') {               
              this.clearValue();
              this.hide();
            } else {                  
              this.store.filter('parentItem', subject.getValue());
                  
              if(level.getValue())
                this.store.filterAdd('level', level.getValue(), true);
                
              if(this.store.find('subject', value) == -1)
                this.clearValue();
                  
              if(this.store.getCount() <= 1) {
                this.clearValue();
                this.hide();
              } else {
                this.show();
              }                                  
            }
            return true;                               
          }
          ,listeners:{
            expand:{
              fn:function(){
                var system = Ext.getCmp('combo-system-'+modName);
                var level = Ext.getCmp('combo-level-'+modName);
                var subject = Ext.getCmp('combo-subject-'+modName);
                    
                if(subject.getValue()) {
                  this.store.filter('parentItem', subject.getValue());
                if(level.getValue())
                  this.store.filterAdd('level', level.getValue(), true);
                } else {                      
                  if (level.getValue())
                    this.store.filter('level', level.getValue(), true);
                }                      
              }
            }
          }
        }]
      },{
        xtype:'fieldset'
        ,title:__('search.fieldset-type.label')
        ,id: 'search-advanced-'+modName+'-type'
        ,autoHeight: true
        ,collapsible: true
        ,collapsed: true
        ,animCollapse: false
        ,border: true
        ,stateful: true
        ,stateEvents: ['expand','collapse']
        ,items:[{
          xtype:'combo'
          ,id:'combo-ict-'+modName
          ,fieldLabel:'ICT'
          ,hideLabel:true
          ,hiddenName:'ict'
          ,width:comboWidth
          ,listWidth:comboListWidth
          ,mode:'local'
          ,store:data.filter.store.ict
          ,displayField:'ict'
          ,valueField:'id'
          ,typeAhead:true
          ,triggerAction:'all'
          ,emptyText:_('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')
          ,validator:function(value){
            if(this.store.find('ict', value) == -1)
              this.clearValue();
            return true;
          }
          ,listeners:{
            select:{
              fn:function(combo, value){
                var subict = Ext.getCmp('combo-subict-'+modName);
                if(subict.getValue() === '')
                  subict.setRawValue('UNSPECIFIED');
                subict.validate();                   
              }
            }
          }
        },{
          xtype:'combo'
          ,fieldLabel:'Sub ICT'
          ,hideLabel:true
          ,id:'combo-subict-'+modName
          ,hiddenName:'subict'
          ,width:comboWidth
          ,listWidth:comboListWidth
          ,mode:'local'
          ,store:data.filter.store.subict
          ,displayField:'ict'
          ,valueField:'id'
          ,typeAhead:true
          ,triggerAction:'all'
          ,emptyText:_('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')
          ,lastQuery:''
          ,hidden:true
          ,hideMode:'visibility'
          ,validator:function(value){
            var ict = Ext.getCmp('combo-ict-'+modName);
            if(ict.getValue() === '') {
              this.clearValue();
              this.hide();
            } else {
            this.store.filter('parentItem', ict.getValue());
            if(this.store.find('ict', value) == -1)
              this.clearValue();
            if(this.store.getCount() <= 1) {
              this.clearValue();
              this.hide();
            } else {
              this.show();
            }     
          }
          return true;
        }
        ,listeners:{
          expand:{
            fn:function(){
              var ict = Ext.getCmp('combo-ict-'+modName);
              this.store.filter('parentItem', ict.getValue());
            }
          }
        }
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.fieldset-other.label')
      ,id: 'search-advanced-'+modName+'-other'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{
        xtype:'combo'
        ,id:'combo-language-'+modName
        ,fieldLabel:'Language'
        ,hideLabel:true
        ,hiddenName:'language'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.language
        ,displayField:'language'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_language_UNSPECIFIED')
      },/*{
        xtype:'combo'
        ,id:'combo-review-'+modName
        ,fieldLabel:'Review'
        ,hideLabel:true
        ,hiddenName:'review'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.review
        ,displayField:'review'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('search.resource.review.selector.UNSPECIFIED')
      },*/{
        xtype:'combo'
        ,id:'combo-special-'+modName
        ,fieldLabel:'Special Filters'
        ,hideLabel:true
        ,hiddenName:'special'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.special
        ,displayField:'special'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('search.resource.special.selector.UNSPECIFIED')
      }]
    },{
      xtype:'button'
      ,id:'search-filterPanel-button'      
      ,text:_('search.resource.filter.button.text')
      ,layout:'anchor'
      ,listeners:{
        click:{
          fn: function(){
            Search.doSearch(modName, true);
          }
        }
      }
    }]
  }     
  
  form.rowExpander = new Ext.grid.RowExpander({
    tpl: new Ext.XTemplate(
      _('search.resource.resource.expanded.title'),
      '<ul>',
      '<tpl for="parents">',
        '<li class="resource-{assetType} category-{category} subcategory-{category}_{subcategory}">',
          '<a href="{[this.getParentURL(values)]}" ext:qtip="{[this.getQtip(values)]}">',
            '{title}',
          '</a>',
        '</li>',
      '</tpl>',
      '</ul>', {
        getParentURL: function(values){
          var page = values.page||false;
          if (page) {
            return '/xwiki/bin/view/'+page.replace(/\./, '/');
          } else {
            return '';
          }
        },
        getQtip: function(values){
          var f = Curriki.module.search.data.resource.filter;

          var desc = Ext.util.Format.stripTags(values.description||'');
          desc = Ext.util.Format.ellipsis(desc, 256);
          desc = Ext.util.Format.htmlEncode(desc);

          var fw = Curriki.data.fw_item.getRolloverDisplay(values.fwItems||[]);
          var lvl = Curriki.data.el.getRolloverDisplay(values.levels||[]);
      
          return String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}"
            ,desc,_('global.title.popup.description')
            ,fw,_('global.title.popup.subject')
            ,lvl,_('global.title.popup.educationlevel')
          );
        }
      }
    )
  });

  form.rowExpander.renderer = function(v, p, record){
    var cls;
    if (record.data.parents && record.data.parents.size() > 0) {
      p.cellAttr = 'rowspan="2"';
      cls = 'x-grid3-row-expander';
//      return '<div class="x-grid3-row-expander">&#160;</div>';
      return String.format('<img class="{0}" src="{1}" ext:qtip="{2}" />', cls, Ext.BLANK_IMAGE_URL, _('search.resource.icon.plus.rollover'));
    } else {
      cls = 'x-grid3-row-expander-empty';
//      return '<div class="x-grid3-row-expander-empty">&#160;</div>';
      return String.format('<img class="{0}" src="{1}" />', cls, Ext.BLANK_IMAGE_URL);
    }
  };

  form.rowExpander.on('expand', function(expander, record, body, idx){
    var row = expander.grid.view.getRow(idx);
    var iconCol = Ext.DomQuery.selectNode('img[class=x-grid3-row-expander]', row);
    Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.minus.rollover')});
  });

  form.rowExpander.on('collapse', function(expander, record, body, idx){
    var row = expander.grid.view.getRow(idx);
    var iconCol = Ext.DomQuery.selectNode('img[class=x-grid3-row-expander]', row);
    Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.plus.rollover')});
  });
  
  form.featuredRowExpander = new Ext.grid.RowExpander({
    tpl: new Ext.XTemplate(
      _('search.resource.resource.expanded.title'),
      '<ul>',
      '<tpl for="parents">',
        '<li class="resource-{assetType} category-{category} subcategory-{category}_{subcategory}">',
          '<a href="{[this.getParentURL(values)]}" ext:qtip="{[this.getQtip(values)]}">',
            '{title}',
          '</a>',
        '</li>',
      '</tpl>',
      '</ul>', {
        getParentURL: function(values){
          var page = values.page||false;
          if (page) {
            return '/xwiki/bin/view/'+page.replace(/\./, '/');
          } else {
            return '';
          }
        },
        getQtip: function(values){
          var f = Curriki.module.search.data.resource.filter;

          var desc = Ext.util.Format.stripTags(values.description||'');
          desc = Ext.util.Format.ellipsis(desc, 256);
          desc = Ext.util.Format.htmlEncode(desc);

          var fw = Curriki.data.fw_item.getRolloverDisplay(values.fwItems||[]);
          var lvl = Curriki.data.el.getRolloverDisplay(values.levels||[]);
      
          return String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}"
            ,desc,_('global.title.popup.description')
            ,fw,_('global.title.popup.subject')
            ,lvl,_('global.title.popup.educationlevel')
          );
        }
      }
    )
  });

  form.featuredRowExpander.renderer = function(v, p, record){
    var cls;
    if (record.data.parents && record.data.parents.size() > 0) {
      p.cellAttr = 'rowspan="2"';
      cls = 'x-grid3-row-expander';
//      return '<div class="x-grid3-row-expander">&#160;</div>';
      return String.format('<img class="{0}" src="{1}" ext:qtip="{2}" />', cls, Ext.BLANK_IMAGE_URL, _('search.resource.icon.plus.rollover'));
    } else {
      cls = 'x-grid3-row-expander-empty';
//      return '<div class="x-grid3-row-expander-empty">&#160;</div>';
      return String.format('<img class="{0}" src="{1}" />', cls, Ext.BLANK_IMAGE_URL);
    }
  };

  form.featuredRowExpander.on('expand', function(expander, record, body, idx){
    var row = expander.grid.view.getRow(idx);
    var iconCol = Ext.DomQuery.selectNode('img[class=x-grid3-row-expander]', row);
    Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.minus.rollover')});
  });

  form.featuredRowExpander.on('collapse', function(expander, record, body, idx){
    var row = expander.grid.view.getRow(idx);
    var iconCol = Ext.DomQuery.selectNode('img[class=x-grid3-row-expander]', row);
    Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.plus.rollover')});
  });
  
  form.featuredPanel = {
    xtype:'grid'
    ,id:'search-results-featured-'+modName
    ,title:__('search.featured-'+modName+'.grid.title')
    ,renderTo:'featured-results'
    ,border:false
    ,autoHeight:true
    ,width:Search.settings.gridWidth
    ,autoExpandColumn:'title'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
    ,hidden:true
    ,viewConfig: {
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
    ,listeners:{      
      beforeshow: {
        fn:function(grid) {
          if(grid.getStore().getTotalCount() == 0)
            return false;
        }
      }
    }   
    ,columnsText:_('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store:data.store.featuredResults
    ,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm:new Ext.grid.ColumnModel([
      Ext.apply(
        form.featuredRowExpander
        ,{})
    ,{
      id:'featured-result'
      ,sortable:false
      ,hideable:false
      ,resizeable:false
      ,renderer: data.featuredRenderer.result
      ,width: 553
      //,tooltip:_('search.preview-external.column.header.preview-result')
    }])
    ,loadMask:false
    ,plugins: form.featuredRowExpander
    ,bbar:new Ext.Toolbar({
      id:'search-preview-results-statusbar'+modName
      ,items:['->',{ 
        text:__('search.preview.statusbar.text.'+modName)
        ,id:'search-featured-link'
        ,handler: function() {
          var category = Ext.getCmp('combo-category-'+modName);
          category.setValue('sankore');
          Ext.getCmp('search-advanced-resource-category').expand();
          form.doSearch();                            
        }  
      }]
    })  
  };

  form.resultsPanel = {
    xtype:'grid'
    ,id:'search-results-'+modName
    ,title:__('search.'+modName+'.grid.title')
    ,border:false
    ,autoHeight:true
    //,width:650
    ,renderTo:'search-results'
    ,autoExpandColumn:'result'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
    ,viewConfig: {
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
    ,columnsText:_('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store: data.store.results
    ,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm: new Ext.grid.ColumnModel([
      Ext.apply(
        form.rowExpander
        ,{
//        tooltip:_('search.resource.icon.plus.title')
        }
      )
    ,{
      id: 'result'
      ,header: '<h3>' + __('search.'+modName+'.tab.title') + '</h3>' //_('search.resource.column.header.result')
      ,menuDisabled:true            
      ,sortable:false
      ,hideable:false
      ,renderer: data.renderer.result
      //,tooltip:_('search.resource.column.header.result')
    }])
    ,loadMask: false
    ,plugins: form.rowExpander    
    ,bbar: new Ext.PagingToolbar({
      id: 'search-pager-'+modName
      ,layout:'xpagingtoolbar'
      ,plugins:new Ext.ux.Andrie.pPageSize({
         variations: [10, 25, 50]
        ,beforeText: _('search.pagination.pagesize.before')
        ,afterText: _('search.pagination.pagesize.after')
        ,addBefore: _('search.pagination.pagesize.addbefore')
        ,addAfter: _('search.pagination.pagesize.addafter')
      })
      ,pageSize: 25
      ,store: data.store.results
      ,displayInfo: true
      ,displayMsg: _('search.pagination.displaying.'+modName)
      ,emptyMsg: _('search.find.no.results')
      ,beforePageText: _('search.pagination.beforepage')
      ,afterPageText: _('search.pagination.afterpage')
      ,firstText: _('search.pagination.first')
      ,prevText: _('search.pagination.prev')
      ,nextText: _('search.pagination.next')
      ,lastText: _('search.pagination.last')
      ,refreshText: _('search.pagination.refresh')
      ,listeners:{
        'change':{
          fn:function(toolbar, page) {
            var featuredPanel = Ext.getCmp('search-results-featured-'+modName);
            if(page.activePage != 1)
              featuredPanel.hide();
            if(page.activePage == 1 && !featuredPanel.isVisible() && Ext.getCmp('combo-category-'+modName).getValue() != 'sankore')
              featuredPanel.show();
            if(page.activePage == page.pages)
              googleDoSearch(modName);
            else
              $('googleSearch').hide();
          }
        }
      }
    })
  };

  form.doSearch = function(){
    var filters = Search.util.getFilters(modName);
    if (filters['category'] != "sankore") {
      filters['category'] = "sankore";
      Search.util.doGridSearch(Ext.getCmp('search-results-featured-'+modName), filters);
    } else {
      Ext.getCmp('search-results-featured-'+modName).hide();
    }    
    
    Search.util.doSearch(modName);
  };

  // Adjust title with count  
  Search.util.registerStoreListeners('featured-'+modName);
  Search.util.registerStoreListeners(modName);
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    form.init();    
  });
});

// TODO:  Register this tab somehow with the main form

})();

Ext.ns('Curriki.module.search.data.external');
(function(){
var modName = 'external';

var data = Curriki.module.search.data.external;

data.init = function(){
  console.log('data.'+modName+': init');

  // Set up filters
  data.filter = {};
  var f = data.filter; // Alias
  
	f.list = ['terms', 'system', 'level', 'sublevel', 'subject', 'subsubject', 'ict', 'subict', 'language', 'review', 'special'];

  f.data = {};
  f.data.system = {
    list: Curriki.data.education_system.list
    ,data: [
      ['AssetMetadata.InternationalEducation', _('CurrikiCode.AssetClass_education_system_AssetMetadata.InternationalEducation')]
    ]
  }  
  f.data.system.list.each(function(item){
    f.data.system.data.push([
      item.id
      ,_('CurrikiCode.AssetClass_education_system_'+item.id)
    ]);
  });

  f.data.subject =  Curriki.module.search.data.resource.filter.data.subject;
  f.data.subsubject =  Curriki.module.search.data.resource.filter.data.subsubject;

  f.data.level =  Curriki.module.search.data.resource.filter.data.level;
  f.data.sublevel =  Curriki.module.search.data.resource.filter.data.sublevel;

  f.data.ict =  Curriki.module.search.data.resource.filter.data.ict;
  f.data.subict =  Curriki.module.search.data.resource.filter.data.subict;
  
  f.data.category =  {
    list: Curriki.data.category.list
    ,data: []
  };
  
  f.data.category.list.each(function(value){
    var sort = _('CurrikiCode.AssetClass_category_'+value);
    if (value === 'unknown') {
      sort = 'zzz';
    }    
    f.data.category.data.push([
      value
      ,_('CurrikiCode.AssetClass_category_'+value)
      ,sort
    ]);    
  });

  f.data.language =  Curriki.module.search.data.resource.filter.data.language;

  f.data.review = {
    list: [
      'partners', 'highest_rated', 'members.highest_rated'
    ]
    ,data: [
      ['', _('search.resource.review.selector.UNSPECIFIED')]
    ]
  };
  f.data.review.list.each(function(review){
    f.data.review.data.push([
      review
      ,_('search.resource.review.selector.'+review)
    ]);
  });

  f.data.special = {
    list: [
      'contributions', 'updated', 'notreviewed'
    ]
    ,data: [
      ['', _('search.resource.special.selector.UNSPECIFIED')]
    ]
  };
  f.data.special.list.each(function(special){
    f.data.special.data.push([
      special
      ,_('search.resource.special.selector.'+special)
    ]);
  });

  f.store = {
    system: new Ext.data.SimpleStore({
      fields: ['id', 'education_system']
      ,data: f.data.system.data
      ,id: 0
    })
    ,level: new Ext.data.SimpleStore({
      fields: ['id', 'level', 'parentItem']
      ,data: f.data.level.data
      ,id: 0
    })
    ,sublevel: new Ext.data.SimpleStore({
      fields: ['id', 'level', 'parentItem']
      ,data: f.data.sublevel.data
      ,id: 0
    })
    ,subject: new Ext.data.SimpleStore({
      fields: ['id', 'subject', 'parentItem', 'level']
      ,data: f.data.subject.data
      ,id: 0
    })
    ,subsubject: new Ext.data.SimpleStore({
      fields: ['id', 'subject', 'parentItem', 'level']
      ,data: f.data.subsubject.data
      ,id: 0
    })
    ,ict: new Ext.data.SimpleStore({
      fields: ['id', 'ict']
      ,data: f.data.ict.data
      ,id: 0
    })
    ,subict: new Ext.data.SimpleStore({
      fields: ['id', 'ict', 'parentItem']
      ,data: f.data.subict.data
      ,id: 0
    })
    ,category: new Ext.data.SimpleStore({
      fields: ['id', 'category', 'sortValue']
      ,sortInfo: {field:'sortValue', direction:'ASC'}
      ,data: f.data.category.data
      ,id: 0
    })
    ,language: new Ext.data.SimpleStore({
      fields: ['id', 'language']
      ,data: f.data.language.data
      ,id: 0
    })
    ,review: new Ext.data.SimpleStore({
      fields: ['id', 'review']
      ,data: f.data.review.data
      ,id: 0
    })
    ,special: new Ext.data.SimpleStore({
      fields: ['id', 'special']
      ,data: f.data.special.data
      ,id: 0
    })
  };

  // Set up data store
  data.store = {};

  data.store.record = new Ext.data.Record.create([
    { name: 'title' }
    ,{name: 'link' }
    ,{ name: 'description' }
    ,{ name: 'assetType' }
    ,{ name: 'contributor' }
    ,{ name: 'contributorName' }
    ,{ name: 'rating', mapping: 'review' }
    ,{ name: 'memberRating', mapping: 'rating' }
    ,{ name: 'ratingCount' }
    ,{ name: 'fwItems' }
    ,{ name: 'levels' }
    ,{ name: 'updated' }
  ]);

  data.store.results = new Ext.data.Store({
    storeId: 'search-store-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/External'
      ,method:'GET'
    })
    ,baseParams: { xpage: "plain", '_dc':(new Date().getTime()), category: 'external' }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  data.store.results.setDefaultSort('memberRating', 'desc');

  // Set up renderers
  data.renderer = {
    result: function(value, metadata, record, rowIndex, colIndex, store){
      var page = record.id.replace(/\./, '/');

      var desc = Ext.util.Format.stripTags(record.data.description);
      desc = Ext.util.Format.ellipsis(desc, 256);
      //desc = Ext.util.Format.htmlEncode(desc);
      
      var title = String.format('<a href="/xwiki/bin/view/{0}">{1}</a>', page, Ext.util.Format.ellipsis(value, 80));
      var link = String.format('<a class="link" href="{0}">{1}</a>', record.data.link, Ext.util.Format.ellipsis(record.data.link, 80));
      
      var memberRating = record.data.memberRating;
      if (memberRating == "")
        memberRating = "0";
      var ratingCount = record.data.ratingCount;
      if (ratingCount == "")
        ratingCount = "0";        
      var rating =  String.format('<span class="rating rating-{0}"><a href="/xwiki/bin/view/{2}?viewer=comments" qtip="{3}">{1} avis </a><a href="/xwiki/bin/view/{2}?viewer=comments"><img class="rating-icon" src="{4}" qtip="{3}" /></a></span>', memberRating, ratingCount, page, _('search.resource.rating.'+memberRating), Ext.BLANK_IMAGE_URL);
      
      var review = String.format('');
      if (record.data.rating != "") {
        review = String.format('<a class="rating review crs-{0}" title="{1}" href="/xwiki/bin/view/{3}?viewer=comments"><span class="crs-text">{1}</span><img class="crs-icon" alt="" src="{2}" /></a>', record.data.rating, _('search.resource.review.'+record.data.rating), Ext.BLANK_IMAGE_URL, page)
      }

      return String.format('{0}{1}<h4 class="title">{2}</h4>{3}<p class="description">{4}</p>', review, rating, title, link, desc);
    }
  };

  data.store.featuredResults = new Ext.data.Store({
    storeId: 'search-store-featured-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/Resources'
      ,method:'GET'
    })
    ,baseParams: { start: '0', limit: '2', xpage: "plain", '_dc':(new Date().getTime()), category:"sankore" }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  data.store.featuredResults.setDefaultSort('rating', 'asc');

  // Set up renderers
  data.featuredRenderer = {
    result: function(value, metadata, record, rowIndex, colIndex, store) {
     var page = record.id.replace(/\./, '/');
      
      var title = String.format('<a href="/xwiki/bin/view/{0}">{1}</a>', page, Ext.util.Format.ellipsis(record.data.title, 80));
      var desc = Ext.util.Format.ellipsis(Ext.util.Format.stripTags(record.data.description), 256);
      var imgsrc = "/xwiki/skins/curriki20/icons/mediatype/";      
      imgsrc = imgsrc + "sankore_large.png";
      var link = String.format('<a class="preview" href="/xwiki/bin/view/{0}"><img src="{1}" /></a>', page, imgsrc);
      var rating = String.format('');
      if (record.data.memberRating != "") {
        rating =  String.format('<span class="rating rating-{0}"><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}">{1} avis </a><a href="/xwiki/bin/view/{2}?viewer=comments"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a></span>', record.data.memberRating, record.data.ratingCount, page, _('search.resource.rating.'+record.data.memberRating), Ext.BLANK_IMAGE_URL);
      }
      var cleanContributorName = '/xwiki/bin/view/' +record.data.contributor.trim().replace('.', '/'); 
      var contributor = String.format('<a class="contributor" href="{0}">{1}</a>', cleanContributorName, record.data.contributorName);
      
      return String.format('{0}{1}<h4 class="title">{2}</h4>{3}<p class="description">{4}</p>', link, rating, title, contributor, desc);
    }
  };
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    data.init();
  });
});
})();

Ext.ns('Curriki.module.search.form.external');
(function(){
var modName = 'external';

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function() {
  console.log('form.'+modName+': init');

  var comboWidth = 180;
  var comboListWidth = 250;

  form.termPanel = Search.util.createTermPanel(modName, form);
  //form.helpPanel = Search.util.createHelpPanel(modName, form);

  form.filterPanel = {
    xtype: 'form'
    ,labelAlign: 'left'
    ,id: 'search-filterPanel-'+modName
    ,formId: 'search-filterForm-'+modName
    ,border: false    
    ,renderTo:'search-filters'    
    ,items:[{
      xtype:'fieldset'
      ,title:__('search.fieldset-category.label')
      ,id: 'search-advanced-'+modName+'-category'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{
        xtype:'combo'
        ,id:'combo-category-'+modName
        ,fieldLabel:'Category'
        ,hideLabel:true
        ,hiddenName:'category'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.category
        ,displayField:'category'
        ,valueField:'id'
        //,plugins:new form.categoryCombo()
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_category_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
        ,value:'external'
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.combo-system.label')
      ,id: 'search-advanced-'+modName+'-system'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: false
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{
        xtype:'combo'
        ,id:'combo-system-'+modName
        ,fieldLabel:'System'
        ,hideLabel:true
        ,hiddenName:'system'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.system
        ,displayField:'education_system'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,selectOnFocus:true
        ,forceSelection:true
        ,value:Curriki.data.education_system.initial
        ,validator:function(value){
          if(this.store.find('education_system', value) == -1)
            this.setRawValue(Curriki.data.education_system.initial);
          return true;
        }
        ,listeners:{
          select:{
            fn:function(combo, value){
              var level = Ext.getCmp('combo-level-'+modName);                                      
              level.clearValue();
              var sublevel = Ext.getCmp('combo-sublevel-'+modName);                                  
              sublevel.clearValue();
              sublevel.hide();
              var subject = Ext.getCmp('combo-subject-'+modName);                                        
              subject.clearValue(); 
              var subsubject = Ext.getCmp('combo-subsubject-'+modName);                                        
              subsubject.clearValue();  
              subsubject.hide();                                                      
            }
          }
        }
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.combo-level.label')
      ,id: 'search-advanced-'+modName+'-level'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{            
        xtype:'combo'
        ,id:'combo-level-'+modName
        ,fieldLabel:'Level'
        ,hideLabel:true
        ,hiddenName:'level'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.level
        ,displayField:'level'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
        ,validator:function(value){
          var system = Ext.getCmp('combo-system-'+modName);                
          this.store.filter('parentItem', system.getValue());
          if(this.store.find('level', value) == -1)
            this.clearValue();                                                   
          return true;
        }
        ,listeners:{                 
          expand:{
            fn:function(combo){
              var system = Ext.getCmp('combo-system-'+modName);
              this.store.filter('parentItem', system.getValue());                   
            } 
          }
          ,select:{
            fn:function(combo, value){
              var sublevel = Ext.getCmp('combo-sublevel-'+modName);
              var subject = Ext.getCmp('combo-subject-'+modName);
              var subsubject = Ext.getCmp('combo-subsubject-'+modName);
                  
              if(sublevel.getValue() === '')
                sublevel.setRawValue('UNSPECIFIED');
              sublevel.validate();
              if(subject.getValue() === '')
                subject.setRawValue('UNSPECIFIED');
              subject.validate();
              if(subsubject.getValue() === '')
                subsubject.setRawValue('UNSPECIFIED');
              subsubject.validate();                                       
            }
          }
        }
      },{
        xtype:'combo'
        ,fieldLabel:'Sub Level'
        ,hideLabel:true
        ,id:'combo-sublevel-'+modName
        ,hiddenName:'sublevel'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.sublevel
        ,displayField:'level'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
        ,lastQuery:''
        ,hidden:true
        ,hideMode:'visibility'
        ,validator:function(value){
          var level = Ext.getCmp('combo-level-'+modName);    
          if(level.getValue() === '') {
            this.clearValue();
            this.hide();
          } else {                           
            this.store.filter('parentItem', level.getValue());
            if(this.store.find('level', value) == -1)
              this.clearValue();
            if(this.store.getCount() <= 1) {
              this.clearValue();
              this.hide();
            } else {
              this.show();
            }
          }                
          return true;          
        }
        ,listeners:{
          expand:{
            fn:function(){
              var level = Ext.getCmp('combo-level-'+modName);
              this.store.filter('parentItem', level.getValue());
            }
          }
        }              
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.combo-subject.label')
      ,id: 'search-advanced-'+modName+'-subject'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{       
        xtype:'combo'
        ,id:'combo-subject-'+modName
        ,fieldLabel:'Subject'
        ,hideLabel:true
        ,hiddenName:'subject'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.subject
        ,displayField:'subject'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
        ,validator:function(value){
          var system = Ext.getCmp('combo-system-'+modName);
          var level = Ext.getCmp('combo-level-'+modName);                                                      
          if(level.getValue())
            this.store.filter('level', level.getValue(), true);
          else
            this.store.filter('parentItem', '-');               
          if(this.store.find('subject', value) == -1)
            this.clearValue();                                        
          return true;
        }
        ,listeners:{
          expand:{
            fn:function(){
              var system = Ext.getCmp('combo-system-'+modName);
              var level = Ext.getCmp('combo-level-'+modName);
              if(level.getValue())
                this.store.filter('level', level.getValue(), true);
              else
                this.store.filter('parentItem', '-');                            
            } 
          }
          ,select:{
            fn:function(combo, value){                                    
              var subsubject = Ext.getCmp('combo-subsubject-'+modName);     
              if(subsubject.getValue() === '')
                subsubject.setRawValue('UNSPECIFIED');                
              subsubject.validate();                    
            }
          }
        }
      },{
        xtype:'combo'
        ,fieldLabel:'Sub subject'
        ,hideLabel:true
        ,id:'combo-subsubject-'+modName
        ,hiddenName:'subsubject'            
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.subsubject
        ,displayField:'subject'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
        ,lastQuery:''
        ,hidden:true
        ,hideMode:'visibility'
        ,validator:function(value){
          var system = Ext.getCmp('combo-system-'+modName);
          var level = Ext.getCmp('combo-level-'+modName);
          var subject = Ext.getCmp('combo-subject-'+modName);
                
          if(subject.getValue() === '') {               
            this.clearValue();
            this.hide();
          } else {                  
            this.store.filter('parentItem', subject.getValue());
                  
            if(level.getValue())
              this.store.filterAdd('level', level.getValue(), true);
                
            if(this.store.find('subject', value) == -1)
              this.clearValue();
                  
            if(this.store.getCount() <= 1) {
              this.clearValue();
              this.hide();
            } else {
              this.show();
            }                                  
          }
          return true;                               
        }
        ,listeners:{
          expand:{
            fn:function(){
              var system = Ext.getCmp('combo-system-'+modName);
              var level = Ext.getCmp('combo-level-'+modName);
              var subject = Ext.getCmp('combo-subject-'+modName);
                    
              if(subject.getValue()) {
                this.store.filter('parentItem', subject.getValue());
              if(level.getValue())
                this.store.filterAdd('level', level.getValue(), true);
              } else {                      
                if (level.getValue())
                  this.store.filter('level', level.getValue(), true);
              }                      
            }
          }
        }
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.fieldset-type.label')
      ,id: 'search-advanced-'+modName+'-type'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{
        xtype:'combo'
        ,id:'combo-ict-'+modName
        ,fieldLabel:'ICT'
        ,hideLabel:true
        ,hiddenName:'ict'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.ict
        ,displayField:'ict'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
        ,validator:function(value){
          if(this.store.find('ict', value) == -1)
            this.clearValue();
          return true;
        }
        ,listeners:{
          select:{
            fn:function(combo, value){
              var subict = Ext.getCmp('combo-subict-'+modName);
              if(subict.getValue() === '')
                subict.setRawValue('UNSPECIFIED');
              subict.validate();                   
            }
          }
        }
      },{
        xtype:'combo'
        ,fieldLabel:'Sub ICT'
        ,hideLabel:true
        ,id:'combo-subict-'+modName
        ,hiddenName:'subict'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.subict
        ,displayField:'ict'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
        ,lastQuery:''
        ,hidden:true
        ,hideMode:'visibility'
        ,validator:function(value){
          var ict = Ext.getCmp('combo-ict-'+modName);
          if(ict.getValue() === '') {
            this.clearValue();
            this.hide();
          } else {
            this.store.filter('parentItem', ict.getValue());
            if(this.store.find('ict', value) == -1)
              this.clearValue();
            if(this.store.getCount() <= 1) {
              this.clearValue();
              this.hide();
            } else {
              this.show();
            }     
          }
          return true;
        }
        ,listeners:{
          expand:{
            fn:function(){
              var ict = Ext.getCmp('combo-ict-'+modName);
              this.store.filter('parentItem', ict.getValue());
            }
          }
        }
      }]
    },{
      xtype:'fieldset'
      ,title:__('search.fieldset-other.label')
      ,id: 'search-advanced-'+modName+'-other'
      ,autoHeight: true
      ,collapsible: true
      ,collapsed: true
      ,animCollapse: false
      ,border: true
      ,stateful: true
      ,stateEvents: ['expand','collapse']
      ,items:[{
        xtype:'combo'
        ,id:'combo-language-'+modName
        ,fieldLabel:'Language'
        ,hideLabel:true
        ,hiddenName:'language'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.language
        ,displayField:'language'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('CurrikiCode.AssetClass_language_UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
      },/*{
        xtype:'combo'
        ,id:'combo-review-'+modName
        ,fieldLabel:'Review'
        ,hideLabel:true
        ,hiddenName:'review'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.review
        ,displayField:'review'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('search.resource.review.selector.UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
      },*/{
        xtype:'combo'
        ,id:'combo-special-'+modName
        ,fieldLabel:'Special Filters'
        ,hideLabel:true
        ,hiddenName:'special'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.special
        ,displayField:'special'
        ,valueField:'id'
        ,typeAhead:true
        ,triggerAction:'all'
        ,emptyText:_('search.resource.special.selector.UNSPECIFIED')
        ,selectOnFocus:true
        ,forceSelection:true
      }]
    }]
  }

  form.featuredPanel = {
    xtype:'grid'
    ,id:'search-results-featured-'+modName
    ,title:__('search.'+modName+'.featured.grid.title')
    ,renderTo:'featured-results'
    ,border:false
    ,autoHeight:true
    ,width:Search.settings.gridWidth
    ,autoExpandColumn:'title'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
		,hidden:true
    ,viewConfig: {
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
		,listeners:{      
      beforeshow: {
        fn:function(grid) {
          if(grid.getStore().getTotalCount() == 0)
            return false;
        }
      }
    }		
    ,columnsText:_('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store:data.store.featuredResults
    ,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm:new Ext.grid.ColumnModel([
    {
      id:'featured-result'
      ,sortable:false
      ,hideable:false
      ,resizeable:false
      ,renderer: data.featuredRenderer.result
      ,width: 553
      //,tooltip:_('search.preview-external.column.header.preview-result')
    }])
    ,loadMask:false
    ,bbar:new Ext.Toolbar({
      id:'search-preview-results-statusbar'+modName
      ,items:['->',{ 
        text:__('search.preview.statusbar.text.'+modName)
        ,id:'search-featured-link'
        ,handler: function() {
					
          var filters = Search.util.getFilters(modName);
          filters['category'] = "sankore";
					var resourceFilters = {};
					resourceFilters['resource'] = Search.util.applyFiltersFor(filters, 'resource');
					resourceFilters = Search.util.applyTermsFor(resourceFilters, Search.util.getTerms(), 'resource');
					var token = {};
          token['s'] = 'resource';
          token['f'] = resourceFilters;
          //token['p'] = {};
					//token['p']['c'] = 0;
					//token['p']['s'] = 25;
					token['a'] = {};
					//token['a']['resource'] = false;

          var provider = new Ext.state.Provider();
          var encodedToken = provider.encodeValue(token);

          // redirect to resource search
          window.location = '/xwiki/bin/view/Search/WebHome#'+encodedToken;
        }  
      }]
    })	
  };

  form.resultsPanel = {
    xtype:'grid'
    ,id:'search-results-'+modName
    ,renderTo:'search-results'
    ,title:__('search.'+modName+'.grid.title')
    ,border:false
    ,autoHeight:true
    ,width:Search.settings.gridWidth
    ,autoExpandColumn:'title'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
    ,viewConfig:{
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
    ,columnsText:_('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store:data.store.results
    ,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm:new Ext.grid.ColumnModel([
    {
      id:'result'
      ,header:_('search.external.column.header.result')
      ,sortable:true
      ,hideable:false
      ,renderer: data.renderer.result
      //,tooltip:_('search.resource.column.header.result')
    }])
    ,loadMask: false    
    ,bbar:new Ext.PagingToolbar({
      id:'search-pager-'+modName
      ,layout:'xpagingtoolbar'
      ,plugins:new Ext.ux.Andrie.pPageSize({
         variations:[10, 25, 50]
        ,beforeText:_('search.pagination.pagesize.before')
        ,afterText:_('search.pagination.pagesize.after')
        ,addBefore:_('search.pagination.pagesize.addbefore')
        ,addAfter:_('search.pagination.pagesize.addafter')
      })
      ,pageSize:25
      ,store:data.store.results
      ,displayInfo:true
      ,displayMsg:_('search.pagination.displaying.'+modName)
      ,emptyMsg:_('search.find.no.results')
      ,beforePageText:_('search.pagination.beforepage')
      ,afterPageText:_('search.pagination.afterpage')
      ,firstText:_('search.pagination.first')
      ,prevText:_('search.pagination.prev')
      ,nextText:_('search.pagination.next')
      ,lastText:_('search.pagination.last')
      ,refreshText:_('search.pagination.refresh')
      ,listeners:{
        'change':{
          fn:function(toolbar, page) {
            var featuredPanel = Ext.getCmp('search-results-featured-'+modName);
            if(page.activePage != 1)
              featuredPanel.hide();
            if(page.activePage == 1 && !featuredPanel.isVisible())
              featuredPanel.show();
            //if(page.activePage == page.pages && Ext.getCmp('search-termPanel-terms').isVisible())
            googleDoSearch(modName);
            //else
            //  $('googleSearch').hide();
          }
        }
      }
    })
  };

  /*
  form.mainPanel = {
    xtype:'panel'
    ,id:'search-panel-'+modName
    ,autoHeight:true
    ,items:[
      form.filterPanel
      ,form.featuredGrid
      ,form.resultsGrid
    ]
  };*/

  form.doSearch = function(){
		
		var filters = Search.util.getFilters(modName);
		
		if (filters['category'] != "external") {
		  var resourceFilters = {};
          resourceFilters['resource'] = Search.util.applyFiltersFor(filters, 'resource');
          resourceFilters = Search.util.applyTermsFor(resourceFilters, Search.util.getTerms(), 'resource');
          var token = {};
          token['s'] = 'resource';
          token['f'] = resourceFilters;
          //token['p'] = {};
          //token['p']['c'] = 0;
          //token['p']['s'] = 25;
          token['a'] = {};
          //token['a']['resource'] = false;

          var provider = new Ext.state.Provider();
          var encodedToken = provider.encodeValue(token);

          // redirect to resource search
          window.location = '/xwiki/bin/view/Search/WebHome#'+encodedToken;
      
		}
		  
		filters['category'] = "sankore";
		Search.util.doGridSearch(Ext.getCmp('search-results-featured-'+modName), filters);
    Search.util.doSearch(modName);
  };

  // Adjust title with count
	Search.util.registerStoreListeners('featured-'+modName);
  Search.util.registerStoreListeners(modName);
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    form.init();
  });
});

})();

Ext.ns('Curriki.module.search.data.group');
(function(){
var modName = 'group';

var data = Curriki.module.search.data.group;

data.init = function(){
  console.log('data.'+modName+': init');


  // Set up filters
  data.filter = {};
  var f = data.filter; // Alias

  f.data = {};

  f.data.system = {
    list: Curriki.data.education_system.list
    ,data: [
      ['AssetMetadata.InternationalEducation', _('CurrikiCode.AssetClass_education_system_AssetMetadata.InternationalEducation')]
    ]
  }  
  f.data.system.list.each(function(item){
    f.data.system.data.push([
      item.id
      ,_('CurrikiCode.AssetClass_education_system_'+item.id)
    ]);
  });

  f.data.level =  {
    mapping: Curriki.data.el.elMap['TREEROOTNODE']
    ,list: []
    ,toplist: []
    ,data: [
      //['', _('CurrikiCode.AssetClass_educational_level_UNSPECIFIED'),'']
    ]
  };
  f.data.level.mapping.each(function(value){
    f.data.level.list.push(value.id);
    if(value.parent == '')
      f.data.level.toplist.push(value.id);
  });
  f.data.system.list.each(function(value){
    f.data.level.data.push([
      ''
      ,_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
      ,value.id
    ]);
  });

  f.data.level.list.each(function(value){
    var parent = '';
    f.data.level.mapping.each(function(item){
      if(value == item.id) {
        if(item.parent == '')
          parent = item.value
        else
          parent = item.parent;
      }
    });
      
    f.data.level.data.push([
      value
      ,_('CurrikiCode.AssetClass_educational_level_'+value)
      ,parent
    ]);
  });
  
  
  f.data.sublevel =  {
    mapping: Curriki.data.el.elMap
    ,data: [
    ]
  };
  f.data.level.mapping.each(function(parentItem){
    f.data.sublevel.data.push([
      parentItem.id
      ,_('CurrikiCode.AssetClass_educational_level_'+parentItem.id+'.UNSPECIFIED')
      ,parentItem.id
    ]);
    if(f.data.sublevel.mapping[parentItem.id]) {
      f.data.sublevel.mapping[parentItem.id].each(function(el){
        f.data.sublevel.data.push([
          el.id
          ,_('CurrikiCode.AssetClass_educational_level_'+el.id)
          ,parentItem.id
        ]);
      });
    }
  });
  
  f.data.subject =  {
    mapping: Curriki.data.fw_item.fwMap['TREEROOTNODE']
    ,list: []
    ,data: [
      ['', Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')),'-','-']
    ]
  };
  f.data.level.toplist.each(function(value){
    f.data.subject.data.push([
      ''
      ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED'))
      ,''
      ,value
    ]);
  });
  f.data.subject.mapping.each(function(value){
    f.data.subject.list.push(value.id);
  });

  f.data.subject.list.each(function(value){
    var parent = '-';
    var level = '';
    f.data.subject.mapping.each(function(item){
      if(value == item.id) {
        //parent = item.parent;
        level = item.value;
      }      
    });
    f.data.subject.data.push([
      value
      ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_'+value))
      ,parent
      ,level
    ]);
  });

  f.data.subsubject =  {
    mapping: Curriki.data.fw_item.fwMap
    ,data: [
    ]
  };
  f.data.subject.mapping.each(function(parentItem){
    f.data.subsubject.data.push([
      parentItem.id
      ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_'+parentItem.id+'.UNSPECIFIED'))
      ,parentItem.id
      ,parentItem.value
    ]);
    f.data.subsubject.mapping[parentItem.id].each(function(subject){
      f.data.subsubject.data.push([
        subject.id
        ,Ext.htmlDecode(_('CurrikiCode.AssetClass_fw_items_'+subject.id))
        ,parentItem.id
        ,subject.value
      ]);
    });
  });

  f.data.policy =  {
    list: ['open', 'closed']
    ,data: [
      ['', _('search.XWiki.SpaceClass_policy_UNSPECIFIED')]
    ]
  };
  f.data.policy.list.each(function(value){
    f.data.policy.data.push([
      value
      ,_('search.XWiki.SpaceClass_policy_'+value)
    ]);
  });

  f.data.language =  {
    list: Curriki.data.language.list
    ,data: [
      ['', _('XWiki.CurrikiSpaceClass_language_UNSPECIFIED')]
    ]
  };
  f.data.language.list.each(function(value){
    f.data.language.data.push([
      value
      ,_('XWiki.CurrikiSpaceClass_language_'+value)
    ]);
  });
  
  // sort the list for the language
  f.data.language.data.sort(function(a, b) {
    // if a or b are head, return as first
    if(b[0] == "") return 1;
    if(a[0] == "") return -1;
    // if a or b are uncategorized, return as last
    if(b[0] == "999") return -1;
    if(a[0] == "999") return 1;
    // compare alphabetically
    if (a[1] <= b[1]) return -1;
      else return 1;
  });

  f.store = {
    system: new Ext.data.SimpleStore({
      fields: ['id', 'education_system']
      ,data: f.data.system.data
      ,id: 0
    })
    ,subject: new Ext.data.SimpleStore({
      fields: ['id', 'subject', 'parentItem', 'level']
      ,data: f.data.subject.data
      ,id: 0
    })

    ,subsubject: new Ext.data.SimpleStore({
      fields: ['id', 'subject', 'parentItem', 'level']
      ,data: f.data.subsubject.data
      ,id: 0
    })

    ,level: new Ext.data.SimpleStore({
      fields: ['id', 'level', 'parentItem']
      ,data: f.data.level.data
      ,id: 0
    })

    ,sublevel: new Ext.data.SimpleStore({
      fields: ['id', 'level', 'parentItem']
      ,data: f.data.sublevel.data
      ,id: 0
    })
    ,policy: new Ext.data.SimpleStore({
      fields: ['id', 'policy']
      ,data: f.data.policy.data
      ,id: 0
    })

    ,language: new Ext.data.SimpleStore({
      fields: ['id', 'language']
      ,data: f.data.language.data
      ,id: 0
    })
  };



  // Set up data store
  data.store = {};

  data.store.record = new Ext.data.Record.create([
    { name: 'title' }
    ,{ name: 'url' }
    ,{ name: 'policy' }
    ,{ name: 'description' }
    ,{ name: 'updated' }
  ]);

  data.store.results = new Ext.data.Store({
    storeId: 'search-store-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/GroupsJSON'
      ,method:'GET'
    })
    ,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  data.store.results.setDefaultSort('title', 'asc');



  // Set up renderers
  data.renderer = {
    title: function(value, metadata, record, rowIndex, colIndex, store){
      return String.format('<a href="{0}">{1}</a>', record.data.url, value);
    }

    ,policy: function(value, metadata, record, rowIndex, colIndex, store){
      if (value !== ''){
        metadata.css = 'policy-'+value;
      }
      var policy = _('search.group.icon.'+value);
      return String.format('<span ext:qtip="{1}">{0}</span>', policy, _('search.group.icon.'+value+'.rollover'));
    }

    ,description: function(value, metadata, record, rowIndex, colIndex, store) {
      var desc = Ext.util.Format.htmlDecode(value);
      desc = Ext.util.Format.stripScripts(value);
      desc = Ext.util.Format.stripTags(desc);
      desc = Ext.util.Format.ellipsis(desc, 128);
      desc = Ext.util.Format.htmlEncode(desc);
      desc = Ext.util.Format.trim(desc);
      return String.format('{0}', desc);
    }

    ,updated: function(value, metadata, record, rowIndex, colIndex, store) {
      var dt = Ext.util.Format.date(value, 'M-d-Y');
      return String.format('{0}', dt);
    }
    ,result: function(value, metadata, record, rowIndex, colIndex, store) {
      
      var title = String.format('<a href="{0}">{1}</a>', record.data.url, Ext.util.Format.ellipsis(record.data.title, 80));
      var imgsrc = "/xwiki/skins/curriki20/curriki/images/groups_default_logo.gif";
      var link = String.format('<a class="preview" href="{0}"><img src="{1}" /></a>', record.data.url, imgsrc);       
      var desc = Ext.htmlDecode(Ext.util.Format.ellipsis(Ext.util.Format.stripTags(record.data.description), 256));
      var policy = String.format('<span qtip="{1}">{0}</span>', _('search.group.icon.' + record.data.policy), _('search.group.icon.'+record.data.policy+'.rollover'));
      
      return String.format('{0}<h4 class="title">{1}</h4>{2}<p class="description">{3}</p>', link, title, policy, desc);
    }
  };
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    data.init();
  });
});
})();

Ext.ns('Curriki.module.search.form.group');
(function(){
var modName = 'group';

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
  console.log('form.'+modName+': init');

  var comboWidth = 195;
  var comboListWidth = 250;

  form.termPanel = Search.util.createTermPanel(modName, form);
//  form.helpPanel = Search.util.createHelpPanel(modName, form);

  form.filterPanel = {
    xtype:'form'
    ,labelAlign:'left'
    ,id:'search-filterPanel-'+modName
    ,formId:'search-filterForm-'+modName
    ,border:false
    ,renderTo:'search-filters' 
    ,items:[{
          xtype:'fieldset'
          ,title:__('search.combo-system.label')
          ,id: 'search-advanced-'+modName+'-system'
          ,autoHeight: true
          ,collapsible: true
          ,collapsed: true
          ,animCollapse: false
          ,border: false
          ,stateful: true
          ,stateEvents: ['expand','collapse']
          ,items:[{
        xtype:'combo'
        ,id:'combo-system-'+modName
        ,fieldLabel:'System'
        ,hideLabel:true
        ,hiddenName:'system'
        ,width:comboWidth
        ,listWidth:comboListWidth
        ,mode:'local'
        ,store:data.filter.store.system
        ,typeAhead:true
        ,valueField:'id'
        ,displayField:'education_system'               
        ,triggerAction:'all'
        ,value:Curriki.data.education_system.initial        
        ,validator:function(value){
          if(this.store.find('education_system', value) == -1)
            this.setValue(Curriki.data.education_system.initial);
          return true;
        }
        ,listeners:{
          select:{
            fn:function(combo, value){
              var level = Ext.getCmp('combo-level-'+modName);                                      
              level.clearValue();
              var sublevel = Ext.getCmp('combo-sublevel-'+modName);                                  
              sublevel.clearValue();
              sublevel.hide();
              var subject = Ext.getCmp('combo-subject-'+modName);                                        
              subject.clearValue(); 
              var subsubject = Ext.getCmp('combo-subsubject-'+modName);                                        
              subsubject.clearValue();  
              subsubject.hide();                                                      
            }
          }
        }
      }]
        },{
          xtype:'fieldset'
          ,title:__('search.combo-level.label')
          ,id: 'search-advanced-'+modName+'-level'
          ,autoHeight: true
          ,collapsible: true
          ,collapsed: true
          ,animCollapse: false
          ,border: true
          ,stateful: true
          ,stateEvents: ['expand','collapse']
          ,items:[{            
            xtype:'combo'
            ,id:'combo-level-'+modName
            ,fieldLabel:'Level'
            ,hideLabel:true
            ,hiddenName:'level'
            ,width:comboWidth
            ,listWidth:comboListWidth
            ,mode:'local'
            ,store:data.filter.store.level
            ,displayField:'level'
            ,valueField:'id'
            ,typeAhead:true
            ,triggerAction:'all'
            ,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
            ,selectOnFocus:true
            ,forceSelection:true
            ,validator:function(value){
              var system = Ext.getCmp('combo-system-'+modName);                
              this.store.filter('parentItem', system.getValue());
              if(this.store.find('level', value) == -1)
                this.clearValue();                                                   
              return true;
            }
            ,listeners:{                 
              expand:{
                fn:function(combo){
                  var system = Ext.getCmp('combo-system-'+modName);
                  this.store.filter('parentItem', system.getValue());                   
                } 
              }
              ,select:{
                fn:function(combo, value){
                  var sublevel = Ext.getCmp('combo-sublevel-'+modName);
                  var subject = Ext.getCmp('combo-subject-'+modName);
                  var subsubject = Ext.getCmp('combo-subsubject-'+modName);
                    
                  if(sublevel.getValue() === '')
                    sublevel.setRawValue('UNSPECIFIED');
                  sublevel.validate();
                  if(subject.getValue() === '')
                    subject.setRawValue('UNSPECIFIED');
                  subject.validate();
                  if(subsubject.getValue() === '')
                    subsubject.setRawValue('UNSPECIFIED');
                  subsubject.validate();                                       
                }
              }
            }
          },{
            xtype:'combo'
            ,fieldLabel:'Sub Level'
            ,hideLabel:true
            ,id:'combo-sublevel-'+modName
            ,hiddenName:'sublevel'
            ,width:comboWidth
            ,listWidth:comboListWidth
            ,mode:'local'
            ,store:data.filter.store.sublevel
            ,displayField:'level'
            ,valueField:'id'
            ,typeAhead:true
            ,triggerAction:'all'
            ,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
            ,selectOnFocus:true
            ,forceSelection:true
            ,lastQuery:''
            ,hidden:true
            ,hideMode:'visibility'
            ,validator:function(value){
              var level = Ext.getCmp('combo-level-'+modName);    
              if(level.getValue() === '') {
                this.clearValue();
                this.hide();
              } else {                           
                this.store.filter('parentItem', level.getValue());
                if(this.store.find('level', value) == -1)
                  this.clearValue();
                if(this.store.getCount() <= 1) {
                  this.clearValue();
                  this.hide();
                } else {
                  this.show();
                }
              }                
              return true;          
            }
            ,listeners:{
              expand:{
                fn:function(){
                  var level = Ext.getCmp('combo-level-'+modName);
                  this.store.filter('parentItem', level.getValue());
                }
              }
            }              
          }]
        },{
          xtype:'fieldset'
          ,title:__('search.combo-subject.label')
          ,id: 'search-advanced-'+modName+'-subject'
          ,autoHeight: true
          ,collapsible: true
          ,collapsed: true
          ,animCollapse: false
          ,border: true
          ,stateful: true
          ,stateEvents: ['expand','collapse']
          ,items:[{       
            xtype:'combo'
            ,id:'combo-subject-'+modName
            ,fieldLabel:'Subject'
            ,hideLabel:true
            ,hiddenName:'subject'
            ,width:comboWidth
            ,listWidth:comboListWidth
            ,mode:'local'
            ,store:data.filter.store.subject
            ,displayField:'subject'
            ,valueField:'id'
            ,typeAhead:true
            ,triggerAction:'all'
            ,emptyText:_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')
            ,selectOnFocus:true
            ,forceSelection:true
            ,validator:function(value){
              var system = Ext.getCmp('combo-system-'+modName);
              var level = Ext.getCmp('combo-level-'+modName);                                                      
              if(level.getValue())
                this.store.filter('level', level.getValue(), true);
              else
                this.store.filter('parentItem', '-');               
              if(this.store.find('subject', value) == -1)
                this.clearValue();                                        
              return true;
            }
            ,listeners:{
              expand:{
                fn:function(){
                  var system = Ext.getCmp('combo-system-'+modName);
                  var level = Ext.getCmp('combo-level-'+modName);
                  if(level.getValue())
                    this.store.filter('level', level.getValue(), true);
                  else
                    this.store.filter('parentItem', '-');                            
                } 
              }
              ,select:{
                fn:function(combo, value){                                    
                  var subsubject = Ext.getCmp('combo-subsubject-'+modName);     
                  if(subsubject.getValue() === '')
                    subsubject.setRawValue('UNSPECIFIED');                
                  subsubject.validate();                    
                }
              }
            }
          },{
            xtype:'combo'
            ,fieldLabel:'Sub subject'
            ,hideLabel:true
            ,id:'combo-subsubject-'+modName
            ,hiddenName:'subsubject'            
            ,width:comboWidth
            ,listWidth:comboListWidth
            ,mode:'local'
            ,store:data.filter.store.subsubject
            ,displayField:'subject'
            ,valueField:'id'
            ,typeAhead:true
            ,triggerAction:'all'
            ,emptyText:_('CurrikiCode.AssetClass_fw_items_UNSPECIFIED')
            ,selectOnFocus:true
            ,forceSelection:true
            ,lastQuery:''
            ,hidden:true
            ,hideMode:'visibility'
            ,validator:function(value){
              var system = Ext.getCmp('combo-system-'+modName);
              var level = Ext.getCmp('combo-level-'+modName);
              var subject = Ext.getCmp('combo-subject-'+modName);
                
              if(subject.getValue() === '') {               
                this.clearValue();
                this.hide();
              } else {                  
                this.store.filter('parentItem', subject.getValue());
                  
                if(level.getValue())
                  this.store.filterAdd('level', level.getValue(), true);
                
                if(this.store.find('subject', value) == -1)
                  this.clearValue();
                  
                if(this.store.getCount() <= 1) {
                  this.clearValue();
                  this.hide();
                } else {
                  this.show();
                }                                  
              }
              return true;                               
            }
            ,listeners:{
              expand:{
                fn:function(){
                  var system = Ext.getCmp('combo-system-'+modName);
                  var level = Ext.getCmp('combo-level-'+modName);
                  var subject = Ext.getCmp('combo-subject-'+modName);
                    
                  if(subject.getValue()) {
                    this.store.filter('parentItem', subject.getValue());
                  if(level.getValue())
                    this.store.filterAdd('level', level.getValue(), true);
                  } else {                      
                    if (level.getValue())
                      this.store.filter('level', level.getValue(), true);
                  }                      
                }
              }
            }
            
          }]
            
       },{
        xtype:'fieldset'
        ,title:__('search.fieldset-other.label')
        ,id: 'search-advanced-'+modName+'-other'
        ,autoHeight: true
        ,collapsible: true
        ,collapsed: true
        ,animCollapse: false
        ,border: true
        ,stateful: true
        ,stateEvents: ['expand','collapse']
        ,items:[{
          xtype:'combo'
          ,id:'combo-language-'+modName
          ,fieldLabel:'Language'
          ,hideLabel:true
          ,hiddenName:'language'
          ,width:comboWidth
          ,listWidth:comboListWidth
          ,mode:'local'
          ,store:data.filter.store.language
          ,displayField:'language'
          ,valueField:'id'
          ,typeAhead:true
          ,triggerAction:'all'
          ,emptyText:_('CurrikiCode.AssetClass_language_UNSPECIFIED')
          ,selectOnFocus:true
          ,forceSelection:true
        },{
              xtype:'combo'
              ,id:'combo-policy-'+modName
              ,fieldLabel:'Membership Policy'
              ,hideLabel:true
              ,hiddenName:'policy'
              ,mode:'local'
              ,width:comboWidth
              ,listWidth:comboListWidth
              ,store:data.filter.store.policy
              ,displayField:'policy'
              ,valueField:'id'
              ,typeAhead:true
              ,triggerAction:'all'
              ,emptyText:_('search.XWiki.SpaceClass_policy_UNSPECIFIED')
              ,selectOnFocus:true
              ,forceSelection:true
            }]
      }]
  }

  /*
  form.columnModel = new Ext.grid.ColumnModel([{
      id: 'policy'
      ,header: _('search.group.column.header.policy')
      ,width: 62
      ,dataIndex: 'policy'
      ,sortable:true
      ,renderer: data.renderer.policy
//      ,tooltip: _('search.group.column.header.policy')
    },{
      id: 'title'
      ,header: _('search.group.column.header.name')
      ,width: 213
      ,dataIndex: 'title'
      ,sortable:true
      ,hideable:false
      ,renderer: data.renderer.title
//      ,tooltip:_('search.group.column.header.name')
    },{
      id: 'description'
      ,width: 217
      ,header: _('search.group.column.header.description')
      ,dataIndex:'description'
      ,sortable:false
      ,renderer: data.renderer.description
//      ,tooltip: _('search.group.column.header.description')
    },{
      id: 'updated'
      ,width: 96
      ,header: _('search.group.column.header.updated')
      ,dataIndex:'updated'
      ,sortable:true
      ,renderer: data.renderer.updated
//      ,tooltip: _('search.group.column.header.updated')
  }]);*/

  form.resultsPanel = {
    xtype:'grid'
    ,id:'search-results-'+modName
    ,title:__('search.'+modName+'.tab.title')
    ,renderTo:'search-results'
    ,border:false
    ,autoHeight:true
    ,width:Search.settings.gridWidth
    ,autoExpandColumn:'result'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
    ,viewConfig: {
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
    ,columnsText:_('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store: data.store.results
    ,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm: new Ext.grid.ColumnModel([{
      id: 'result'
      ,header: '<h3>' + __('search.'+modName+'.tab.title') + '</h3>' //_('search.resource.column.header.result')
      ,menuDisabled:true            
      ,sortable:false
      ,hideable:false
      ,renderer: data.renderer.result
      //,tooltip:_('search.resource.column.header.result')
    }])
    ,loadMask: false
    ,plugins: form.rowExpander
    ,bbar: new Ext.PagingToolbar({
      id: 'search-pager-'+modName
      ,plugins:new Ext.ux.Andrie.pPageSize({
         variations: [10, 25, 50]
        ,beforeText: _('search.pagination.pagesize.before')
        ,afterText: _('search.pagination.pagesize.after')
        ,addBefore: _('search.pagination.pagesize.addbefore')
        ,addAfter: _('search.pagination.pagesize.addafter')
      })
      ,pageSize: 25
      ,store: data.store.results
      ,displayInfo: true
      ,displayMsg: __('search.pagination.displaying.'+modName)
      ,emptyMsg: __('search.find.no.results')
      ,beforePageText: _('search.pagination.beforepage')
      ,afterPageText: _('search.pagination.afterpage')
      ,firstText: _('search.pagination.first')
      ,prevText: _('search.pagination.prev')
      ,nextText: _('search.pagination.next')
      ,lastText: _('search.pagination.last')
      ,refreshText: _('search.pagination.refresh')
    })
  };

  //form.mainPanel = {
  //  xtype:'panel'
  //  ,id:'search-panel-'+modName
  //  ,autoHeight:true
  //  ,items:[
  //    form.filterPanel
  //    ,form.resultsPanel
  //  ]
  //};

  form.doSearch = function(){
    Search.util.doSearch(modName);
  };

  // Adjust title with count
  Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    form.init();
  });
});


// TODO:  Register this tab somehow with the main form

})();

Ext.ns('Curriki.module.search.data.member');
(function(){
var modName = 'member';

var data = Curriki.module.search.data.member;

data.init = function(){
  console.log('data.'+modName+': init');

  // Set up filters
  data.filter = {};
  var f = data.filter; // Alias

  f.data = {};

  f.data.subject =  {
    mapping: Curriki.data.fw_item.fwMap['TREEROOTNODE']
    ,list: []
    ,data: [
      ['', _('XWiki.XWikiUsers_topics_FW_masterFramework.UNSPECIFIED')]
    ]
  };
  f.data.subject.mapping.each(function(value){
    f.data.subject.list.push(value.id);
  });
  f.data.subject.list.each(function(value){
    f.data.subject.data.push([
      value
      ,_('XWiki.XWikiUsers_topics_'+value)
    ]);
  });

  // sort the list for the subject
  f.data.subject.data.sort(function(a, b) { 
    // if a or b are head, return as first
    if(b[0] == "") return 1; 
    if(a[0] == "") return -1;
    // if a or b are uncategorized, return as last
    if(b[0] == "UNCATEGORIZED") return -1; 
    if(a[0] == "UNCATEGORIZED") return 1;
    // compare alphabetically
    if (a[1] <= b[1]) return -1; 
      else return 1;
  });

  f.data.subsubject =  {
    mapping: Curriki.data.fw_item.fwMap
    ,data: [
    ]
  };
  f.data.subject.mapping.each(function(parentItem){
    f.data.subsubject.data.push([
      parentItem.id
      ,_('XWiki.XWikiUsers_topics_'+parentItem.id+'.UNSPECIFIED')
      ,parentItem.id
    ]);
    f.data.subsubject.mapping[parentItem.id].each(function(subject){
      f.data.subsubject.data.push([
        subject.id
        ,_('XWiki.XWikiUsers_topics_'+subject.id)
        ,parentItem.id
      ]);
    });
  });

  // sort the list for the subsubject
  f.data.subsubject.data.sort(function(a, b) {
    // b is the subject index, put first
    if(b[0] == b[2]) return 1;
    // a is the subject index, put first
    if(a[0] == a[2]) return -1;
    // compare alphabetically
    if (a[1] <= b[1]) return -1;
      else return 1;
  });

  f.data.country =  {
    list: 'AD|AE|AF|AL|AM|AO|AQ|AR|AT|AU|AZ|BA|BB|BD|BE|BF|BG|BH|BI|BJ|BM|BN|BO|BR|BS|BT|BW|BY|BZ|CA|CD|CF|CG|CH|CI|CL|CM|CN|CO|CR|CU|CV|CY|CZ|DE|DJ|DK|DO|DZ|EC|EE|EG|ES|ET|FI|FR|GA|GB|GE|GF|GH|GI|GL|GM|GN|GP|GQ|GR|GT|GW|GY|HK|HN|HR|HT|HU|ID|IE|IL|IN|IQ|IR|IS|IT|JM|JO|JP|KE|KG|KH|KM|KP|KR|KW|KZ|LA|LB|LI|LK|LR|LS|LT|LU|LV|MA|MC|MD|ME|MG|ML|MN|MO|MQ|MR|MT|MU|MV|MW|MX|MY|MZ|NA|NC|NE|NG|NI|NL|NO|NP|NZ|PA|PE|PF|PG|PH|PK|PL|PR|PS|PT|PY|RO|RS|RU|RW|SA|SB|SC|SD|SE|SG|SI|SK|SL|SM|SN|SO|SR|SV|SY|SZ|TD|TG|TH|TJ|TM|TN|TR|TT|TW|TZ|UA|UG|US|UY|UZ|VA|VE|VN|YE|ZA|ZM|ZW'.split('|')
    ,data: [
      ['', _('XWiki.XWikiUsers_country_UNSPECIFIED')]
    ]
  };
  f.data.country.list.each(function(value){
    f.data.country.data.push([
      value
      ,_('XWiki.XWikiUsers_country_'+value)
    ]);
  });

  // sort the list for the language
  f.data.country.data.sort(function(a, b) {
    // if a or b are head, return as first
    if(b[0] == "") return 1;
    if(a[0] == "") return -1;
    // if a or b are uncategorized, return as last
    if(b[0] == "999") return -1;
    if(a[0] == "999") return 1;
    // compare alphabetically
    if (a[1] <= b[1]) return -1;
      else return 1;
  });

  f.data.member_type =  {
    list: ['parent', 'teacher', 'professional', 'student']
    ,data: [
      ['', _('XWiki.XWikiUsers_member_type_UNSPECIFIED')]
    ]
  };
  f.data.member_type.list.each(function(value){
    f.data.member_type.data.push([
      value
      ,_('XWiki.XWikiUsers_member_type_'+value)
    ]);
  });

  f.store = {
    subject: new Ext.data.SimpleStore({
      fields: ['id', 'subject']
      ,data: f.data.subject.data
      ,id: 0
    })

    ,subsubject: new Ext.data.SimpleStore({
      fields: ['id', 'subject', 'parentItem']
      ,data: f.data.subsubject.data
      ,id: 0
    })

    ,member_type: new Ext.data.SimpleStore({
      fields: ['id', 'member_type']
      ,data: f.data.member_type.data
      ,id: 0
    })

    ,country: new Ext.data.SimpleStore({
      fields: ['id', 'country']
      ,data: f.data.country.data
      ,id: 0
    })
  };



  // Set up data store
  data.store = {};

  data.store.record = new Ext.data.Record.create([
    { name: 'name1' }
    ,{ name: 'name2' }
    ,{ name: 'url' }
    ,{ name: 'bio' }
    ,{ name: 'picture' }
    ,{ name: 'contributions' }
  ]);

  data.store.results = new Ext.data.Store({
    storeId: 'search-store-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/Members'
      ,method:'GET'
    })
    ,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  data.store.results.setDefaultSort('name1', 'asc');



  // Set up renderers
  data.renderer = {
    name1: function(value, metadata, record, rowIndex, colIndex, store){
      return String.format('<a href="{1}">{0}</a>', value, record.data.url);
    }

    ,name2: function(value, metadata, record, rowIndex, colIndex, store){
      return String.format('<a href="{1}">{0}</a>', value, record.data.url);
    }

    ,picture: function(value, metadata, record, rowIndex, colIndex, store){
      //TODO: Remove specialized style
      return String.format('<a href="{2}"><img src="{0}" alt="{1}" class="member-picture" style="width:88px" /></a>', value, _('search.member.column.picture.alt.text'), record.data.url);
    }

    ,contributions: function(value, metadata, record, rowIndex, colIndex, store){
      return String.format('{0}', value);
    }

    ,bio: function(value, metadata, record, rowIndex, colIndex, store){
      var desc = Ext.util.Format.htmlDecode(value);
      desc = Ext.util.Format.stripScripts(value);
      desc = Ext.util.Format.stripTags(desc);
      desc = Ext.util.Format.ellipsis(desc, 128);
      desc = Ext.util.Format.htmlEncode(desc);
      desc = Ext.util.Format.trim(desc);
      return String.format('{0}', desc);
    }
  };
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    data.init();
  });
});
})();

Ext.ns('Curriki.module.search.form.member');
(function(){
var modName = 'member';

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
  console.log('form.'+modName+': init');

  var comboWidth = 140;
  var comboListWidth = 250;

  form.termPanel = Search.util.createTermPanel(modName, form);
//  form.helpPanel = Search.util.createHelpPanel(modName, form);

  form.filterPanel = {
    xtype:'form'
    ,labelAlign:'left'
    ,id:'search-filterPanel-'+modName
    ,formId:'search-filterForm-'+modName
    ,border:false
    ,items:[
      form.termPanel
//      ,form.helpPanel
      ,{
        xtype:'fieldset'
        ,title:_('search.advanced.search.button')
        ,id:'search-advanced-'+modName
        ,autoHeight:true
        ,collapsible:true
        ,collapsed:true
        ,animCollapse:false
        ,border:true
        ,stateful:true
        ,stateEvents:['expand','collapse']
        ,listeners:{
          'statesave':{
            fn:Search.util.fieldsetPanelSave
          }
          ,'staterestore':{
            fn:Search.util.fieldsetPanelRestore
          }
          ,'expand':{
            fn:function(panel){
              // CURRIKI-2989
              //  - Force a refresh of the grid view, as this
              //    seems to make the advanced search fieldset
              //    visible in IE7
              Ext.getCmp('search-results-'+modName).getView().refresh();

              Ext.select('.x-form-field-wrap', false, 'search-advanced-'+modName).setWidth(comboWidth);

              // CURRIKI-2873
              // - Force a repaint of the fieldset
              Ext.getCmp('search-termPanel').el.repaint();
            }
          }
          ,'collapse':{
            fn:function(panel){
              Ext.getCmp('search-results-'+modName).getView().refresh();
              Ext.getCmp('search-termPanel').el.repaint();
            }
          }
        }
        ,items:[{
          layout:'column'
          ,border:false
          ,defaults:{
            border:false
            ,hideLabel:true
          }
          ,items:[{
            columnWidth:0.33
            ,layout:'form'
            ,defaults:{
              hideLabel:true
            }
            ,items:[{
              xtype:'combo'
              ,id:'combo-subject-'+modName
              ,fieldLabel:'Subject'
              ,hiddenName:'subjectparent'
              ,width:comboWidth
              ,listWidth:comboListWidth
              ,mode:'local'
              ,store:data.filter.store.subject
              ,displayField:'subject'
              ,valueField:'id'
              ,typeAhead:true
              ,triggerAction:'all'
              ,emptyText:_('XWiki.XWikiUsers_topics_TREEROOTNODE.UNSPECIFIED')
              ,selectOnFocus:true
              ,forceSelection:true
              ,listeners:{
                select:{
                  fn:function(combo, value){
                    var subSubject = Ext.getCmp('combo-subsubject-'+modName);
                    if (combo.getValue() === '') {
                      subSubject.clearValue();
                      subSubject.hide();
                    } else {
                      subSubject.show();
                      subSubject.clearValue();
                      subSubject.store.filter('parentItem', combo.getValue());
                      subSubject.setValue(combo.getValue());
                    }
                  }
                }
              }
            },{
              xtype:'combo'
              ,fieldLabel:'Sub Subject'
              ,id:'combo-subsubject-'+modName
              ,hiddenName:'subject'
              ,width:comboWidth
              ,listWidth:comboListWidth
              ,mode:'local'
              ,store:data.filter.store.subsubject
              ,displayField:'subject'
              ,valueField:'id'
              ,typeAhead:true
              ,triggerAction:'all'
  //            ,emptyText:'Select a Sub Subject...'
              ,selectOnFocus:true
              ,forceSelection:true
              ,lastQuery:''
              ,hidden:true
              ,hideMode:'visibility'
            }]
          },{
            columnWidth:0.33
            ,layout:'form'
            ,defaults:{
              hideLabel:true
            }
            ,items:[{
              xtype:'combo'
              ,id:'combo-member_type-'+modName
              ,fieldLabel:'Member Type'
              ,mode:'local'
              ,width:comboWidth
              ,listWidth:comboListWidth
              ,store:data.filter.store.member_type
              ,hiddenName:'member_type'
              ,displayField:'member_type'
              ,valueField:'id'
              ,typeAhead:true
              ,triggerAction:'all'
              ,emptyText:_('XWiki.XWikiUsers_member_type_UNSPECIFIED')
              ,selectOnFocus:true
              ,forceSelection:true
            }]
          },{
            columnWidth:0.34
            ,layout:'form'
            ,defaults:{
              hideLabel:true
            }
            ,items:[{
              xtype:'combo'
              ,id:'combo-country-'+modName
              ,fieldLabel:'Country'
              ,hiddenName:'country'
              ,width:comboWidth
              ,listWidth:comboListWidth
              ,mode:'local'
              ,store:data.filter.store.country
              ,displayField:'country'
              ,valueField:'id'
              ,typeAhead:true
              ,triggerAction:'all'
              ,emptyText:_('XWiki.XWikiUsers_country_UNSPECIFIED')
              ,selectOnFocus:true
              ,forceSelection:true
            }]
          }]
        }]
      }
    ]
  }

  form.columnModelList = [{
      id: 'picture'
      ,header: _('search.member.column.header.picture')
      ,width: 116
      ,dataIndex: 'picture'
      ,sortable:false
      ,resizable:false
      ,menuDisabled:true
      ,renderer: data.renderer.picture
//      ,tooltip:_('search.member.column.header.picture')
    },{
      id: 'name1'
      ,header: _('search.member.column.header.name1')
      ,width: 120
      ,dataIndex: 'name1'
      ,sortable:true
      ,hideable:false
      ,renderer: data.renderer.name1
//      ,tooltip:_('search.member.column.header.name1')
    },{
      id: 'name2'
      ,width: 120
      ,header: _('search.member.column.header.name2')
      ,dataIndex:'name2'
      ,sortable:true
      ,hideable:false
      ,renderer: data.renderer.name2
//      ,tooltip: _('search.member.column.header.name2')
    },{
      id: 'bio'
      ,width: 120
      ,header: _('search.member.column.header.bio')
      ,dataIndex:'bio'
      ,sortable:false
      ,renderer: data.renderer.bio
//      ,tooltip: _('search.member.column.header.bio')
    },{
      id: 'contributions'
      ,width: 120
      ,header: _('search.member.column.header.contributions')
      ,dataIndex:'contributions'
      ,sortable:false
      ,renderer: data.renderer.contributions
//      ,tooltip: _('search.member.column.header.contributions')
  }];

  form.columnModel = new Ext.grid.ColumnModel(form.columnModelList);

  form.resultsPanel = {
    xtype:'grid'
    ,id:'search-results-'+modName
    //,title:'Results'
    ,border:false
    ,autoHeight:true
    ,width:Search.settings.gridWidth
    ,autoExpandColumn:'bio'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
    ,viewConfig: {
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
    ,columnsText:__('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store: data.store.results
    ,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm: form.columnModel
    ,loadMask: false
    ,plugins: form.rowExpander
    ,bbar: new Ext.PagingToolbar({
      id: 'search-pager-'+modName
      ,plugins:new Ext.ux.Andrie.pPageSize({
         variations: [10, 25, 50]
        ,beforeText: _('search.pagination.pagesize.before')
        ,afterText: _('search.pagination.pagesize.after')
        ,addBefore: _('search.pagination.pagesize.addbefore')
        ,addAfter: _('search.pagination.pagesize.addafter')
      })
      ,pageSize: 25
      ,store: data.store.results
      ,displayInfo: true
      ,displayMsg: _('search.pagination.displaying.'+modName)
      ,emptyMsg: _('search.find.no.results')
      ,beforePageText: _('search.pagination.beforepage')
      ,afterPageText: _('search.pagination.afterpage')
      ,firstText: _('search.pagination.first')
      ,prevText: _('search.pagination.prev')
      ,nextText: _('search.pagination.next')
      ,lastText: _('search.pagination.last')
      ,refreshText: _('search.pagination.refresh')
    })
  };

  form.mainPanel = {
    xtype:'panel'
    ,id:'search-panel-'+modName
    ,autoHeight:true
    ,items:[
      form.filterPanel
      ,form.resultsPanel
    ]
  };

  form.doSearch = function(){
    Search.util.doSearch(modName);
  };

  // Adjust title with count
  Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    form.init();
  });
});


// TODO:  Register this tab somehow with the main form

})();

Ext.ns('Curriki.module.search.data.blog');
(function(){
var modName = 'blog';

var data = Curriki.module.search.data.blog;

data.init = function(){
  console.log('data.'+modName+': init');

  // No filters for blog search

  // Set up data store
  data.store = {};

  data.store.record = new Ext.data.Record.create([
    { name: 'name' }
    ,{ name: 'title' }
    ,{ name: 'text' }
    ,{ name: 'comments' }
    ,{ name: 'updated' }
    ,{ name: 'memberUrl' }
    ,{ name: 'blogUrl' }
  ]);

  data.store.results = new Ext.data.Store({
    storeId: 'search-store-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/Blogs'
      ,method:'GET'
    })
    ,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  data.store.results.setDefaultSort('updated', 'desc');



  // Set up renderers
  data.renderer = {
    name: function(value, metadata, record, rowIndex, colIndex, store){
      return String.format('<a href="{1}">{0}</a>', value, record.data.memberUrl);
    }

    ,text: function(value, metadata, record, rowIndex, colIndex, store){
      var desc = Ext.util.Format.htmlDecode(value); // Reverse conversion
      desc = Ext.util.Format.stripScripts(value);
      desc = Ext.util.Format.stripTags(desc);
      desc = Ext.util.Format.trim(desc);
      desc = Ext.util.Format.ellipsis(desc, 128);
      //desc = Ext.util.Format.htmlEncode(desc);
      return String.format('<a href="{2}" class="search-blog-title">{1}</a><br /><br />{0}', desc, record.data.title, record.data.blogUrl);
    }

    ,comments: function(value, metadata, record, rowIndex, colIndex, store){
      return String.format('{0}', value);
    }

    ,updated: function(value, metadata, record, rowIndex, colIndex, store){
      var dt = Ext.util.Format.date(value, 'M-d-Y');
      return String.format('{0}', dt);
    }
  };
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    data.init();
  });
});
})();

Ext.ns('Curriki.module.search.form.blog');
(function(){
var modName = 'blog';

Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
  console.log('form.'+modName+': init');

  form.termPanel = Search.util.createTermPanel(modName, form);

  form.filterPanel = {
    xtype:'form'
    ,labelAlign:'left'
    ,id:'search-filterPanel-'+modName
    ,formId:'search-filterForm-'+modName
    ,border:false
    ,items:[
      form.termPanel
    ]
  };

  form.columnModel = new Ext.grid.ColumnModel([
    {
      id: 'name'
      ,header:_('search.blog.column.header.name')
      ,width: 160
      ,dataIndex: 'name'
      ,sortable:true
      ,renderer: data.renderer.name
//      ,tooltip:_('search.blog.column.header.name')
    },{
      id: 'text'
      ,header: _('search.blog.column.header.text')
      ,width: 260
      ,dataIndex: 'text'
      ,sortable:false
      ,renderer: data.renderer.text
//      ,tooltip:_('search.blog.column.header.text')
    },{
      id: 'comments'
      ,header: _('search.blog.column.header.comments')
      ,width: 80
      ,dataIndex: 'comments'
      ,sortable:false
      ,renderer: data.renderer.comments
//      ,tooltip:_('search.blog.column.header.comments')
    },{
      id: 'updated'
      ,width: 96
      ,header: _('search.blog.column.header.updated')
      ,dataIndex:'updated'
      ,sortable:true
      ,renderer: data.renderer.updated
//      ,tooltip: _('search.blog.column.header.updated')
  }]);

  form.resultsPanel = {
    xtype:'grid'
    ,id:'search-results-'+modName
    //,title:'Results'
    ,border:false
    ,autoHeight:true
    ,width:Search.settings.gridWidth
    ,autoExpandColumn:'text'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
    ,viewConfig: {
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
    ,columnsText:__('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store: data.store.results
    ,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm: form.columnModel
    ,loadMask: false
    ,bbar: new Ext.PagingToolbar({
      id: 'search-pager-'+modName
      ,plugins:new Ext.ux.Andrie.pPageSize({
         variations: [10, 25, 50]
        ,beforeText: _('search.pagination.pagesize.before')
        ,afterText: _('search.pagination.pagesize.after')
        ,addBefore: _('search.pagination.pagesize.addbefore')
        ,addAfter: _('search.pagination.pagesize.addafter')
      })
      ,pageSize: 25
      ,store: data.store.results
      ,displayInfo: true
      ,displayMsg: _('search.pagination.displaying.'+modName)
      ,emptyMsg: _('search.find.no.results')
      ,beforePageText: _('search.pagination.beforepage')
      ,afterPageText: _('search.pagination.afterpage')
      ,firstText: _('search.pagination.first')
      ,prevText: _('search.pagination.prev')
      ,nextText: _('search.pagination.next')
      ,lastText: _('search.pagination.last')
      ,refreshText: _('search.pagination.refresh')
    })
  };

  form.mainPanel = {
    xtype:'panel'
    ,id:'search-panel-'+modName
    ,autoHeight:true
    ,items:[
      form.filterPanel
      ,form.resultsPanel
    ]
  };

  form.doSearch = function(){
    Search.util.doSearch(modName);
  };

  // Adjust title with count
  Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    form.init();
  });
});


// TODO:  Register this tab somehow with the main form

})();

Ext.ns('Curriki.module.search.data.curriki');
(function(){
var modName = 'curriki';

var data = Curriki.module.search.data.curriki;

data.init = function(){
  console.log('data.'+modName+': init');

  // No filters for curriki search

  // Set up data store
  data.store = {};

  data.store.record = new Ext.data.Record.create([
    { name: 'name' }
//    ,{ name: 'text' }
    ,{ name: 'updated' }
    ,{ name: 'url' }
  ]);

  data.store.results = new Ext.data.Store({
    storeId: 'search-store-'+modName
    ,proxy: new Ext.data.HttpProxy({
      url: '/xwiki/bin/view/Search/Curriki'
      ,method:'GET'
    })
    ,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

    ,reader: new Ext.data.JsonReader({
      root: 'rows'
      ,totalProperty: 'resultCount'
//      ,id: 'page'
    }, data.store.record)

    // turn on remote sorting
    ,remoteSort: true
  });
  data.store.results.setDefaultSort('name', 'asc');



  // Set up renderers
  data.renderer = {
    name: function(value, metadata, record, rowIndex, colIndex, store){
      return String.format('<a href="{1}">{0}</a>', value, record.data.url);
    }

/*
    ,text: function(value, metadata, record, rowIndex, colIndex, store){
      var desc = Ext.util.Format.stripScripts(value);
      desc = Ext.util.Format.stripTags(desc);
      desc = Ext.util.Format.ellipsis(desc, 128);
      desc = Ext.util.Format.htmlEncode(desc);
      return String.format('{0}', desc);
    }
*/

    ,updated: function(value, metadata, record, rowIndex, colIndex, store){
      var dt = Ext.util.Format.date(value, 'M-d-Y');
      return String.format('{0}', dt);
    }
  };
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    data.init();
  });
});
})();

Ext.ns('Curriki.module.search.form.curriki');
(function(){
var modName = 'curriki';

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
  console.log('form.'+modName+': init');

  form.termPanel = Search.util.createTermPanel(modName, form);

  form.filterPanel = {
    xtype:'form'
    ,labelAlign:'left'
    ,id:'search-filterPanel-'+modName
    ,formId:'search-filterForm-'+modName
    ,border:false
    ,items:[
      form.termPanel
    ]
  };

  form.columnModel = new Ext.grid.ColumnModel([
    {
      id: 'name'
      ,header:_('search.curriki.column.header.name')
      ,width: 500
      ,dataIndex: 'name'
      ,sortable:true
      ,renderer: data.renderer.name
//      ,tooltip:_('search.curriki.column.header.name')
    },{
      id: 'updated'
      ,width: 96
      ,header: _('search.curriki.column.header.updated')
      ,dataIndex:'updated'
      ,sortable:true
      ,renderer: data.renderer.updated
//      ,tooltip: _('search.curriki.column.header.updated')
  }]);

  form.resultsPanel = {
    xtype:'grid'
    ,id:'search-results-'+modName
    //,title:'Results'
    ,border:false
    ,autoHeight:true
    ,width:Search.settings.gridWidth
    ,autoExpandColumn:'name'
    ,stateful:true
    ,frame:false
    ,stripeRows:true
    ,viewConfig: {
      forceFit:true
      ,enableRowBody:true
      ,showPreview:true
      // Remove the blank space on right of grid (reserved for scrollbar)
      ,scrollOffset:0
    }
    ,columnsText:__('search.columns.menu.columns')
    ,sortAscText:_('search.columns.menu.sort_ascending')
    ,sortDescText:_('search.columns.menu.sort_descending')
    ,store: data.store.results
    ,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
    ,cm: form.columnModel
    ,loadMask: false
    ,bbar: new Ext.PagingToolbar({
      id: 'search-pager-'+modName
      ,plugins:new Ext.ux.Andrie.pPageSize({
         variations: [10, 25, 50]
        ,beforeText: _('search.pagination.pagesize.before')
        ,afterText: _('search.pagination.pagesize.after')
        ,addBefore: _('search.pagination.pagesize.addbefore')
        ,addAfter: _('search.pagination.pagesize.addafter')
      })
      ,pageSize: 25
      ,store: data.store.results
      ,displayInfo: true
      ,displayMsg: _('search.pagination.displaying.'+modName)
      ,emptyMsg: _('search.find.no.results')
      ,beforePageText: _('search.pagination.beforepage')
      ,afterPageText: _('search.pagination.afterpage')
      ,firstText: _('search.pagination.first')
      ,prevText: _('search.pagination.prev')
      ,nextText: _('search.pagination.next')
      ,lastText: _('search.pagination.last')
      ,refreshText: _('search.pagination.refresh')
    })
  };

  form.mainPanel = {
    xtype:'panel'
    ,id:'search-panel-'+modName
    ,autoHeight:true
    ,items:[
      form.filterPanel
      ,form.resultsPanel
    ]
  };

  form.doSearch = function(){
    Search.util.doSearch(modName);
  };

  // Adjust title with count
  Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
    form.init();
  });
});


// TODO:  Register this tab somehow with the main form

})();

Ext.ns('Curriki.module.search.form');
(function(){

var Search = Curriki.module.search;
var forms = Search.form;

Search.init = function(){
  console.log('search: init');
  
  Curriki.enableLoadingMask('xcontent');
  
  if (Ext.isEmpty(Search.initialized)) {
    if (Ext.isEmpty(Search.tabList)) {
      Search.tabList = ['resource'];
    }
    
    Ext.each(Search.tabList, function(tab){
      var termPanel = new Ext.form.FormPanel(forms[tab].termPanel);
      termPanel.render();
      var filterPanel = new Ext.form.FormPanel(forms[tab].filterPanel);
      filterPanel.render();
      if (forms[tab].featuredPanel) {
        var featuredPanel = new Ext.grid.GridPanel(forms[tab].featuredPanel);
        if (featuredPanel) featuredPanel.render();
      }      
            
      var resultsPanel = new Ext.grid.GridPanel(forms[tab].resultsPanel);
      resultsPanel.render();                 
    });

    var comboWidth = 140;

    Search.doSearch = function(searchTab, resetPage /* default false */, onlyHistory /* default false */) {

      if (Ext.isEmpty(searchTab)) {
	  	  Ext.each(Search.tabList, function(tab){
	  		  var module = forms[tab];
	  		  if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch)) {
						
						
					// Ignore history of advanced search
			    // var advancedSearch = Ext.getCmp('search-advanced-'+tab);
			    //if (!Ext.isEmpty(advancedSearch)) {
			     //  if (!advancedSearch.collapsed) {
			       //  panelSettings[tab] = {a:false}; // Advanced closed
			      // }
			    // }
						
						// Do the search
						module.doSearch();
					}
				});
			} else {
				forms[searchTab].doSearch();
			}
				
      // Adds to history, don't need that
      //var token = {};
      //token['s'] = Ext.isEmpty(searchTab)?'all':searchTab;
      //token['f'] = filterValues;
      //token['p'] = pagerValues;
      //if (Ext.getCmp('search-tabPanel').getActiveTab) {
      //  token['t'] = Ext.getCmp('search-tabPanel').getActiveTab().id;
     // }
      //token['a'] = panelSettings;

      //var provider = new Ext.state.Provider();
      //var encodedToken = provider.encodeValue(token);
      //console.log('Saving History', {values: token});
      //Search.history.setLastToken(encodedToken);
      //Ext.History.add(encodedToken);
    };
        
    /*
    Search.tabPanel = {
      xtype:(Search.tabList.size()>1?'tab':'')+'panel'
      ,id:'search-tabPanel'
      ,activeTab:0
      ,deferredRender:false
      ,autoHeight:true
      ,layoutOnTabChange:true
      ,frame:false
      ,border:false
      ,plain:true
      ,defaults:{
        autoScroll:false
        ,border:false
      }
      ,listeners:{
        tabchange:function(tabPanel, tab){
          // Log changing to view a tab
          var tabId = tab.id.replace(/(^search-|-tab$)/g, '');
          Curriki.logView('/features/search/'+tabId);

          var advancedPanel = Ext.getCmp('search-advanced-'+tabId);
          if (!Ext.isEmpty(advancedPanel)) {
            if (!advancedPanel.collapsed) {
              Ext.select('.x-form-field-wrap', false, 'search-advanced-'+tabId).setWidth(comboWidth);
            }
          }
          //var URLtoken = Ext.History.getToken();
          //var provider = new Ext.state.Provider();
          //var token = provider.decodeValue(URLtoken);
          //token['t'] = tabPanel.getActiveTab().id;
          //console.log('Saving History', {values: token});
          //Ext.History.add(provider.encodeValue(token));        
          Curriki.module.EventManager.fireEvent('Curriki.module.search:tabchange', tabId); 
        }
      }
      ,items:[] // Filled in based on tabs available
    };
    Ext.each(
      Search.tabList
      ,function(tab){
        panel = {
          //title: _('search.'+tab+'.tab.title')
          id:'search-'+tab+'-tab'
          ,cls:'search-'+tab
          ,autoHeight:true
        };
        module = forms[tab];
        if (!Ext.isEmpty(module) && !Ext.isEmpty(module.mainPanel)) {
          panel.items = [module.mainPanel];
          Search.tabPanel.items.push(panel);
        }
      }
    );*/

    /*
    Search.mainPanel = {
      el:'search-div'
      //,title:_('search.top_titlebar')
      ,border:false
      ,height:'600px'
      ,defaults:{border:false}
      ,cls:'search-module'
      ,items:[
        Search.tabPanel
      ]
    };*/   
    
    Ext.ns('Curriki.module.search.history');
    var History = Search.history;
    History.lastHistoryToken = false;

    // Handle this change event in order to restore the UI
    // to the appropriate history state
    History.historyChange = function(token){
      if(token){
        if(token == History.lastHistoryToken){
          // Ignore duplicate tokens
        } else {
          History.updateFromHistory(token);
        }
      } else {
        // TODO:
        // This is the initial default state.
        // Necessary if you navigate starting from the
        // page without any existing history token params
        // and go back to the start state.
      }
    };
		
		History.addToken = function(token) {
			if (token) {
				if (token != History.lastHistoryToken) {
					History.setLastToken(token);
					Ext.History.add(token);
				}
			}
		}

    History.setLastToken = function(token){
      History.lastHistoryToken = token;
    };

    History.updateFromHistory = function(token){
      var provider =  new Ext.state.Provider();
      var values = provider.decodeValue(token);
      console.log('Got History', {token: token, values: values});

      if (!Ext.isEmpty(values)) {
        var filterValues = values['f'];        
        if (!Ext.isEmpty(filterValues) && filterValues['all'] && Ext.getCmp('search-termPanel') && Ext.getCmp('search-termPanel').getForm) {
          Ext.getCmp('search-termPanel').getForm().setValues(filterValues['all']);
        }

        var pagerValues = values['p'];

        var panelSettings = values['a'];

        //if (values['t']) {
        //  if (Ext.getCmp('search-tabPanel').setActiveTab) {
        //    Ext.getCmp('search-tabPanel').setActiveTab(values['t']);
        //  }
        //}

        Ext.each(Search.tabList, function(tab){
          console.log('Updating '+tab);
          var module = Search.form[tab];
          if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch) && !Ext.isEmpty(filterValues) && !Ext.isEmpty(filterValues[tab])) {
            var filterPanel = Ext.getCmp('search-filterPanel-'+tab);
            if (!Ext.isEmpty(filterPanel)) {
              var filterForm = filterPanel.getForm();
              if (!Ext.isEmpty(filterForm)) {
                try {
                  filterForm.setValues(filterValues[tab]);                    
                } catch(e) {
                  console.log('ERROR Updating '+tab, e);
                }
              }
            }

            // Open advanced panel if specified
            //if (!Ext.isEmpty(panelSettings) && !Ext.isEmpty(panelSettings[tab]) && panelSettings[tab].a) {
            //  var advancedPanel = Ext.getCmp('search-advanced-'+tab);
            //  if (!Ext.isEmpty(advancedPanel)) {
            //    advancedPanel.expand(false);
            //  }
            //}
              
              // Update terms value in the form
              if (tab == 'resource' || tab == 'external') {
                var rTerms = values['f'][tab]['terms'];
                if(Ext.getCmp('search-termPanel').getForm() && !Ext.isEmpty(rTerms)) {
                  Ext.getCmp('search-termPanel').getForm().setValues({'terms' : rTerms});
                }
                
                // Update filters panel : collapse / expand
              var resourceFilterLevels = ['category', 'system', 'level', 'subject', 'type', 'other'];
              for(var i=0; i<resourceFilterLevels.length; i++) {
                // Check in history if filter level/sublevel were selected
                var resourceFilterLevel = resourceFilterLevels[i];
                if(!Ext.isEmpty(values['f'][tab][resourceFilterLevel]) || 
                   !Ext.isEmpty(values['f'][tab]['sub' + resourceFilterLevel])) {
                  Ext.getCmp('search-advanced-'+tab+'-' + resourceFilterLevel).expand();
                }
              }
              }   
              
                         

            // Set pager values
            //var pagerPanel = Ext.getCmp('search-pager-'+tab);
            //if (!Ext.isEmpty(pagerPanel) && !Ext.isEmpty(pagerValues)) {
              //if (pagerValues[tab]) {
                //try {
                  //if (pagerValues[tab]['c']) {
                    //pagerPanel.cursor = pagerValues[tab]['c'];
                  //}
                  //if (pagerValues[tab]['s']) {
                    //if (pagerPanel.pageSize != pagerValues[tab]['s']) {
                      //pagerPanel.setPageSize(pagerValues[tab]['s']);
                    //}
                  //}
                //} catch(e) {
                  //console.log('ERROR Updating '+tab, e);
                //}
              //}
            //}
          }
        });

        if (values['s']) {
          console.log('Starting search');
          if (values['s'] === 'all') {
            Search.doSearch();
          } else {
            Search.doSearch(values['s']);
          }
        }

        History.setLastToken(token);
      }
    };


    History.init = function(){
      if (Ext.isEmpty(History.initialized)) {
        var URLtoken = Ext.History.getToken(); // Get BEFORE init'd
        Ext.History.init(
          function(){
            Ext.History.on('change', History.historyChange);

            if (URLtoken) {
              History.historyChange(URLtoken);
            }
          }
        );

        History.initialized = true;
      };
    };

    Search.initialized = true;
    console.log('search: init done');
  }
};

Search.display = function(){
  Search.init();
  Search.history.init();
  Ext.each(Search.tabList, function(tab){ Search.doSearch(tab); });  
};

Search.start = function(){
  Ext.onReady(function(){
    Curriki.data.EventManager.on('Curriki.data:ready', function(){
      Search.display();
    });
  });
};
})();
