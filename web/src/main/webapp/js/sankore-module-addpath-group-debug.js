Ext.ns('Curriki.module.addpath.group');
Curriki.module.addpath.group.init = function() {
    var AddPath = Curriki.module.addpath;
    
    AddPath.group.CloseDone = function(dialog){
      return {
           text:_('add.finalmessage.close.button')
          ,id:'closebutton'
          ,cls:'button button-confirm'
          ,listeners:{
            'click':{
               fn:function(e,evt){
                this.close();
                if (!Ext.isEmpty(Curriki.group.current.cameFrom)){
                  window.location.href=Curriki.group.current.cameFrom;
                }
              }
              ,scope:dialog
            }
          }
      };
    }
    
    AddPath.group.DoneMetadata = Ext.extend(Curriki.ui.dialog.Messages, {
      initComponent:function(){
        Ext.apply(this, {
          title:_('add.finalmessage.title_metadata')
          ,cls:'addpath addpath-done resource resource-add'
          ,bbar:[                     
            '->'
            ,AddPath.group.CloseDone(this)
          ]
          ,items:[
            AddPath.DoneMessage('metadata')
          ]
        });
        AddPath.group.DoneMetadata.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apDoneGroupMetadata', AddPath.group.DoneMetadata);
    
    AddPath.group.Link = function(linkMode) {            
      var link = '/xwiki/bin/view/' + 'Group_' + Curriki.group.current.info.groupName + '/WebHome';
      return '<a href="http://sankore.devxwiki.com' + link + '" target="_blank">' + _('add.finalmessage.' + linkMode + '.link') +  '</a>';    
    }
    
    AddPath.group.DoneGroupAdd = Ext.extend(Curriki.ui.dialog.Messages, {
      initComponent:function(){
        Ext.apply(this, {
          title:_('add.finalmessage.title.groupadd')
          ,cls:'addpath addpath-done resource resource-add'
          ,bbar:[
            AddPath.group.Link('view')                     
            ,'->'
            ,AddPath.group.CloseDone(this)
          ]
          ,items:[
            AddPath.DoneMessage('groupadd')
          ]
        });
        AddPath.group.DoneGroupAdd.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apDoneGroupAdd', AddPath.group.DoneGroupAdd);
    
    
    
    AddPath.group.ShowDone = function(){
      if (Ext.isEmpty(Curriki.group.current.flow)) {
        return;
      }

      //var pageCreated = (Curriki.group.current.groupName&&Curriki.current.asset.assetPage)||Curriki.current.assetName;
      //Curriki.logView('/features/resources/add/'+Curriki.current.flow+Curriki.current.flowFolder+'/'+((Curriki.current.asset&&Curriki.current.asset.assetType)||Curriki.current.assetType||'UNKNOWN')+'/'+pageCreated.replace('.', '/'));

      Curriki.init(function(){
        var p = Ext.ComponentMgr.create({
           xtype:'apDoneGroup'+Curriki.group.current.flow
          ,id:'done-dialogue'
        });
        p.show();
        Ext.ComponentMgr.register(p);
      });
    }
    
    AddPath.GroupMetadata1 = Ext.extend(Curriki.ui.dialog.Actions, {
      initComponent:function(){
        Ext.apply(this, {
           title:_('group.add.setrequiredinfo.part1.title')
          ,cls:'addpath addpath-metadata resource resource-add'          
          ,width:800
          ,items:[{
             xtype:'panel'
            ,cls:'guidingquestion-container'
            ,items:[{
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,html:_('group.add.setrequiredinfo.part1.guidingquestion')
                ,cls:'guidingquestion'
              }
            }]
          },{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.cancel.button')
              ,id:'cancelbutton'
              ,cls:'button button-cancel mgn-rt'
              ,listeners:{
                click:{
                   fn: function(){
                    this.close();
                    window.location.href = Curriki.group.current.cameFrom;
                  }
                  ,scope:this
                }
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-current', html:'1'}
                ,{tag:'a', cls:'addpath-page circle', html:'2'}
                ,{tag:'a', cls:'addpath-page circle', html:'3'}
                ,{tag:'a', cls:'addpath-page circle', html:'4'}
                ,{tag:'a', cls:'addpath-page circle', html:'5'}                
                ]                
            },'->',{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    var form = this.findByType('form')[0].getForm();                                       
                    if(!Curriki.group.current.sri)
                      Curriki.group.current.sri = form.getValues(false);
                    else
                      Ext.apply(Curriki.group.current.sri, form.getValues(false));                     

                    this.close();

                    var p = Ext.ComponentMgr.create({'xtype':'apGM2'});
                    p.show();
                    Ext.ComponentMgr.register(p);                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
    // Title
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,id:'metadata-title'
                ,cls:'information-header information-header-required'
                ,children:[{
                   tag:'span'
                  ,id:'metadata-title-title'
                  ,cls:'metadata-title'
                  ,html:_('group.sri.title.title')
                },{
                   tag:'img'
                  ,id:'metadata-title-info'
                  ,cls:'metadata-tooltip'
                  ,src:Curriki.ui.InfoImg
                  ,qtip:_('group.sri.title.tooltip')
                }]
              }
            },{
               xtype:'textfield'
              ,id:'metadata-title-entry'
              ,name:'title'
              ,emptyText:_('group.sri.title.empty')
              ,allowBlank:false
              ,preventMark:true
              ,hideLabel:true
              ,width:'80%'
              ,value:Curriki.group.current.sri?Curriki.group.current.sri.title:''

    // Description
            },{
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,id:'metadata-description'
                ,cls:'information-header information-header-required'
                ,children:[{
                   tag:'span'
                  ,id:'metadata-description-title'
                  ,cls:'metadata-title'
                  ,html:_('group.sri.description.title')
                },{
                   tag:'img'
                  ,id:'metadata-description-info'
                  ,cls:'metadata-tooltip'
                  ,src:Curriki.ui.InfoImg
                  ,qtip:_('group.sri.description.tooltip')
                }]
              }
            },{
               xtype:'textarea'
              ,id:'metadata-description-entry'
              ,name:'description'
              ,emptyText:_('group.sri.description.empty')
              ,allowBlank:false
              ,preventMark:true
              ,hideLabel:true
              ,width:'80%'
              ,value:Curriki.group.current.sri?Curriki.group.current.sri.description:''              
              },{
    // Url shortcut
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,id:'metadata-urlshortcut'
                ,cls:'information-header information-header-required'
                ,children:[{
                   tag:'span'
                  ,id:'metadata-urlshortcut-title'
                  ,cls:'metadata-urlshortcut'
                  ,html:_('group.sri.urlshortcut.title')
                },{
                   tag:'img'
                  ,id:'metadata-urlshortcut-info'
                  ,cls:'metadata-tooltip'
                  ,src:Curriki.ui.InfoImg
                  ,qtip:_('group.sri.urlshortcut.tooltip')
                }]
              }
            },{
               xtype:'textfield'
              ,id:'metadata-urlshortcut-entry'
              ,name:'urlshortcut'
              ,emptyText:_('group.sri.urlshortcut.empty')
              ,allowBlank:true
              ,preventMark:true
              ,hideLabel:true
              ,width:'80%'
              ,value:Curriki.group.current.sri?Curriki.group.current.sri.urlshortcut:''}]
            }]
        });

        AddPath.GroupMetadata1.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGM1', AddPath.GroupMetadata1);


    AddPath.GroupMetadata2 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part2.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                  this.close();
                  var p = Ext.ComponentMgr.create({'xtype':'apGM1'});
                  p.show();
                }
                ,scope:this
                }                  
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM1'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'2'}
                ,{tag:'a', cls:'addpath-page circle', html:'3'}
                ,{tag:'a', cls:'addpath-page circle', html:'4'}
                ,{tag:'a', cls:'addpath-page circle', html:'5'}                
                ]                
            },'->',{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    var form = this.findByType('form')[0].getForm();                                               
                    Ext.apply(Curriki.group.current.sri, form.getValues(false));                   

                    this.close();

                    var p = Ext.ComponentMgr.create({'xtype':'apGM3'});
                    p.show();
                    Ext.ComponentMgr.register(p);                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
    // Education System
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-education_system'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-education_system-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.education_system.title')
                    },{
                       tag:'img'
                      ,id:'metadata-education_system-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.education_system.tooltip')
                    }]
                  }
                },{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.education_system.txt')
                    ,cls:'directions'
                  }
                },{
                   xtype:'combo'
                  ,id:'metadata-education_system-entry'
                  ,hiddenName:'education_system'
                  ,hideLabel:true
                  ,width:'60%'
                  ,mode:'local'
                  ,store:Curriki.data.education_system.store
                  ,displayField:'education_system'
                  ,valueField:'id'
                  ,typeAhead:true
                  ,triggerAction:'all'
                  ,emptyText:_('group.sri.education_system.empty')
                  ,selectOnFocus:true
                  ,forceSelection:true
                  ,value:Curriki.group.current.sri.education_system?Curriki.group.current.sri.education_system
                    :Curriki.data.education_system.initial?Curriki.data.education_system.initial:undefined
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGM2').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.education_system)){
                            Ext.getCmp('metadata-education_system-entry').setValue(md.education_system);
                          }
                        }
                      })
                    }
                  }
                }]

    // Language
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-language'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-language-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.language.title')
                    },{
                       tag:'img'
                      ,id:'metadata-language-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.language.tooltip')
                    }]
                  }
                },{
                   xtype:'combo'
                  ,id:'metadata-language-entry'
                  ,hiddenName:'language'
                  ,hideLabel:true
                  ,width:'60%'
                  ,mode:'local'
                  ,store:Curriki.data.language.store
                  ,displayField:'language'
                  ,valueField:'id'
                  ,typeAhead:true
                  ,triggerAction:'all'
                  ,emptyText:_('group.sri.language.empty')
                  ,selectOnFocus:true
                  ,forceSelection:true
                  ,value:Curriki.group.current.sri.language?Curriki.group.current.sri.language
                    :Curriki.data.language.initial?Curriki.data.language.initial:undefined
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGM2').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.language)){
                            Ext.getCmp('metadata-language-entry').setValue(md.language);
                          }
                        }
                      })
                    }
                  }
                }]
              }]
            }]
        });

        AddPath.GroupMetadata2.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGM2', AddPath.GroupMetadata2);
    
    AddPath.GroupMetadata3 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part3.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                  this.close();
                  var p = Ext.ComponentMgr.create({'xtype':'apGM2'});
                  p.show();
                }
                ,scope:this
                }                  
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM1'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'2', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM2'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'3'}
                ,{tag:'a', cls:'addpath-page circle', html:'4'}
                ,{tag:'a', cls:'addpath-page circle', html:'5'}                
                ]                
            },'->',{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    //var form = this.findByType('form')[0].getForm();
                    //Ext.apply(Curriki.group.current.sri, form.getValues(false));
                    Curriki.group.current.sri.educational_level = this.findByType('curriki-treepanel')[0].getChecked('id');                     

                    this.close();

                    var p = Ext.ComponentMgr.create({'xtype':'apGM4'});
                    p.show();
                    Ext.ComponentMgr.register(p);                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
    // Educational Level
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.educational_level.txt')
                    ,cls:'directions'
                  }
                },{
                  // A "TreeCheckBoxGroup" would be nice here
                   xtype:'numberfield'
                  ,id:'educational_level-validation'
                  ,allowBlank:false
                  ,preventMark:true
                  ,minValue:1
                  ,hidden:true
                  ,listeners:{
                     valid:function(field){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('el-tree');
                      fieldset.removeClass('x-form-invalid');
                      fieldset.el.dom.qtip = '';
                    }
                    ,invalid:function(field, msg){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('el-tree');
                      fieldset.addClass('x-form-invalid');
                      var iMsg = field.invalidText;
                      fieldset.el.dom.qtip = iMsg;
                      fieldset.el.dom.qclass = 'x-form-invalid-tip';
                      if(Ext.QuickTips){ // fix for floating editors interacting with DND
                        Ext.QuickTips.enable();
                      }

                    }
                  }
                }
                ,(function(){
                  var checkedCount = 0;
                  var md = Curriki.group.current.metadata;
                  if (md) {
                    var el = md.educational_level;
                    Ext.isArray(el) && (function(ca){
                      var childrenFn = arguments.callee;
                      Ext.each(ca, function(c){
                        if (c.id) {
                          if (c.checked = (el.indexOf(c.id) !== -1)) {
                            checkedCount++;
                          }
                          if (c.children) {
                            childrenFn(c.children);
                          }
                        }
                      });
                    })(Curriki.data.el.elChildren);
                  }
                  return Ext.apply(AddPath.elTree = Curriki.ui.component.asset.getElTree(Curriki.group.current.sri.education_system), {
                    listeners: {
                      render:function(comp){
                        comp.findParentByType('apGM3').on('show', function() {
                          Ext.getCmp('educational_level-validation').setValue(checkedCount)
                        });                       
                      }
                      ,resize: function(comp) {
                        var ct = comp.findParentByType('apGM3');
                        ct.syncSize();
                      }
                    }
                  })
                })()] 
              }]
            }]   
        });

        AddPath.GroupMetadata3.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGM3', AddPath.GroupMetadata3);
    
    AddPath.GroupMetadata4 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part4.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,autoScroll:false
          ,autoHeight:true
          ,resizable:false
          ,listeners: {
            resize: function(ct) {
              console.log('window resize');
              var cmp = ct;
              }
            ,bodyresize: function(ct) {
              console.log('window bodyresize');
              var cmp = ct;
              }
          }
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,listeners: {
              resize: function(ct) {
                console.log("form resize");
                var cmp = ct.ownerCt;
                }                     
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                  this.close();
                  var p = Ext.ComponentMgr.create({'xtype':'apGM3'});
                  p.show();
                }
                ,scope:this
                }                  
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM1'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'2', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM2'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'3', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM3'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'4'}
                ,{tag:'a', cls:'addpath-page circle', html:'5'}                
                ]                
            },'->',{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    //var form = this.findByType('form')[0].getForm();
                    
                    //Ext.apply(Curriki.group.current.sri, form.getValues(false));
                    Curriki.group.current.sri.fw_items = this.findByType('curriki-treepanel')[0].getChecked('id');

                    this.close();

                    var p = Ext.ComponentMgr.create({'xtype':'apGM5'});
                    p.show();
                    Ext.ComponentMgr.register(p);                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            /*
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    //if (height === 'auto') {
                      //fPanel.setHeight('auto');
                   // } else {
                   //   fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    //}
                  }
                );
              }              
            }*/
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.fw_items_txt')
                    ,cls:'directions'
                  }
                },{
                  // A "TreeCheckBoxGroup" would be nice here
                   xtype:'numberfield'
                  ,id:'fw_items-validation'
                  ,allowBlank:false
                  ,preventMark:true
                  ,minValue:1
                  ,hidden:true
                  ,listeners:{
                     valid:function(field){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('fw_items-tree');
                      fieldset.removeClass('x-form-invalid');
                      fieldset.el.dom.qtip = '';
                    }
                    ,invalid:function(field, msg){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('fw_items-tree');
                      fieldset.addClass('x-form-invalid');
                      var iMsg = field.invalidText;
                      fieldset.el.dom.qtip = iMsg;
                      fieldset.el.dom.qclass = 'x-form-invalid-tip';
                      if(Ext.QuickTips){ // fix for floating editors interacting with DND
                        Ext.QuickTips.enable();
                      }

                    }
                  }
                }
                ,(function(){
                  var checkedCount = 0;
                  var md = Curriki.group.current.metadata;
                  if (md) {
                    var fw = md.fw_items;
                    Ext.isArray(fw) && (function(ca){
                      var childrenFn = arguments.callee;
                      Ext.each(ca, function(c){
                        if (c.id) {
                          if (c.checked = (fw.indexOf(c.id) !== -1)) {
                            checkedCount++;
                          }
                          if (c.children) {
                            childrenFn(c.children);
                          }
                        }
                      });
                    })(Curriki.data.fw_item.fwChildren);
                  }
                  return Ext.apply(AddPath.fwTree = Curriki.ui.component.asset.getFwTree(Curriki.group.current.sri.educational_level), {
                    listeners: {
                      render:function(comp){
                        comp.findParentByType('apGM4').on('show', function() {
                          Ext.getCmp('fw_items-validation').setValue(checkedCount)
                        });
                      }
                      ,resize: function(comp) {
                        var ct = comp.findParentByType('apGM4');
                        ct.syncSize();
                      }
                    }
                  })
                })()]
            }]   
        });

        AddPath.GroupMetadata4.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGM4', AddPath.GroupMetadata4);               
        
    AddPath.GroupMetadata5 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part5.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                  this.close();
                  var p = Ext.ComponentMgr.create({'xtype':'apGM4'});
                  p.show();
                }
                ,scope:this
                }                  
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM1'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'2', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM2'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'3', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM3'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'4', listeners:{
                  click: {
                    fn: function(e, ev) {
                      this.close();
                      var p = Ext.ComponentMgr.create({'xtype':'apGM4'});
                      p.show();
                    }
                    ,scope:this
                  }
                }}                
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'5'}
                ]                
            },'->',{
               text:_('add.setrequiredinfo.publish.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    var form = this.findByType('form')[0].getForm();                                          
                    
                    Ext.apply(Curriki.group.current.sri, form.getValues(false));  

                    this.close();

                    AddPath.GroupMetadataFinished();                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
// Access level              
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-access_level'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-access_level-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.access_level.title')
                    },{
                       tag:'img'
                      ,id:'metadata-access_level-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.access_level.tooltip')
                    }]
                  }
                },{
                   border:false
                  ,xtype:'radiogroup'
                  ,width:588
                  ,columns:[.95]
                  ,vertical:true
                  ,defaults:{
                    name:'access_level'
                  }
                  ,items:Curriki.data.access_level.radios
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGM5').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.access_level)){
                            Ext.getCmp(Ext.select('input[type="radio"][name="access_level"][value="'+md.access_level+'"]').first().dom.id).setValue(md.access_level);
                          }
                        }
                      })
                    }
                  }
                }]
// Policy                
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-policy'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-policy-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.policy.title')
                    },{
                       tag:'img'
                      ,id:'metadata-policy-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.policy.tooltip')
                    }]
                  }
                },{
                   border:false
                  ,xtype:'radiogroup'
                  ,width:588
                  ,columns:[.95]
                  ,vertical:true
                  ,defaults:{
                    name:'policy'
                  }
                  ,items:Curriki.data.policy.radios
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGM5').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.access_level)){
                            Ext.getCmp(Ext.select('input[type="radio"][name="policy"][value="'+md.policy+'"]').first().dom.id).setValue(md.policy);
                          }
                        }
                      })
                    }
                  }
                }]                           
    // License Deed
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-license'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-license-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.license.title')
                    },{
                       tag:'img'
                      ,id:'metadata-license-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.license.tooltip')
                    }]
                  }
                },{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.license.txt')
                    ,cls:'directions'
                  }
                },{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:_('groul.sri.license.heading')
                  }
                },{
                   xtype:'combo'
                  ,id:'metadata-license-entry'
                  ,hiddenName:'license'
                  ,hideLabel:true
                  ,width:460
                  ,mode:'local'
                  ,store:Curriki.data.license.store
                  ,displayField:'license'
                  ,valueField:'id'
                  ,typeAhead:true
                  ,triggerAction:'all'
                  ,emptyText:_('group.sri.license.empty')
                  ,selectOnFocus:true
                  ,forceSelection:true
                  ,value:Curriki.data.license.initial
                    ?Curriki.data.license.initial
                    :undefined
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGM5').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.licenseType)){
                            Ext.getCmp('metadata-license-entry').setValue(md.licenseType);
                          }
                        }
                      })
                    }
                  }
                }]                
            }]
          }]
        });

        AddPath.GroupMetadata5.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGM5', AddPath.GroupMetadata5);   
    
    AddPath.GroupMetadata = {};
    AddPath.GroupMetadata.SetMetadata = function(cmp){
      var form = cmp.findById('MetadataDialoguePanel').getForm();            
      if(cmp.findById('el-tree')) {
        Curriki.group.current.sri.educational_level = cmp.findById('el-tree').getChecked('id');
      } else if(cmp.findById('fw_items-tree')) { 
        Curriki.group.current.sri.fw_items = cmp.findById('fw_items-tree').getChecked('id');      
      } else if(!Curriki.group.current.sri) {
        Curriki.group.current.sri = form.getValues(false);
      } else {                                   
        Ext.apply(Curriki.group.current.sri, form.getValues(false));
      }
    }
    
    AddPath.GroupMetadata.Show = function(dlg) {
      var p = Ext.ComponentMgr.create({'xtype':dlg});
      p.show();
      Ext.ComponentMgr.register(p);
    }
   
    AddPath.GroupMetadataEdit1 = Ext.extend(Curriki.ui.dialog.Actions, {
      initComponent:function(){
        Ext.apply(this, {
           title:_('group.add.setrequiredinfo.part1.title')
          ,cls:'addpath addpath-metadata resource resource-add'          
          ,width:800
          ,items:[{
             xtype:'panel'
            ,cls:'guidingquestion-container'
            ,items:[{
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,html:_('group.add.setrequiredinfo.part1.guidingquestion')
                ,cls:'guidingquestion'
              }
            }]
          },{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME2');                                       
                  }
                  ,scope:this
                }
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-current', html:'1'}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'2', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME2');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'3', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME3');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'4', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME4');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'5', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME5');
                    }
                    ,scope:this
                  }
                }}               
                ]                
            },'->',{
               text:_('add.setrequiredinfo.publish.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadataEditFinished();                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
    // Title
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,id:'metadata-title'
                ,cls:'information-header information-header-required'
                ,children:[{
                   tag:'span'
                  ,id:'metadata-title-title'
                  ,cls:'metadata-title'
                  ,html:_('group.sri.title.title')
                },{
                   tag:'img'
                  ,id:'metadata-title-info'
                  ,cls:'metadata-tooltip'
                  ,src:Curriki.ui.InfoImg
                  ,qtip:_('group.sri.title.tooltip')
                }]
              }
            },{
               xtype:'textfield'
              ,id:'metadata-title-entry'
              ,name:'title'
              ,emptyText:_('group.sri.title.empty')
              ,allowBlank:false
              ,preventMark:true
              ,hideLabel:true
              ,width:'80%'
              ,value:Curriki.group.current.sri?Curriki.group.current.sri.title:''

    // Description
            },{
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,id:'metadata-description'
                ,cls:'information-header information-header-required'
                ,children:[{
                   tag:'span'
                  ,id:'metadata-description-title'
                  ,cls:'metadata-title'
                  ,html:_('group.sri.description.title')
                },{
                   tag:'img'
                  ,id:'metadata-description-info'
                  ,cls:'metadata-tooltip'
                  ,src:Curriki.ui.InfoImg
                  ,qtip:_('group.sri.description.tooltip')
                }]
              }
            },{
               xtype:'textarea'
              ,id:'metadata-description-entry'
              ,name:'description'
              ,emptyText:_('group.sri.description.empty')
              ,allowBlank:false
              ,preventMark:true
              ,hideLabel:true
              ,width:'80%'
              ,value:Curriki.group.current.sri?Curriki.group.current.sri.description:''              
              },{
    // Url shortcut
               xtype:'box'
              ,autoEl:{
                 tag:'div'
                ,id:'metadata-urlshortcut'
                ,cls:'information-header information-header-required'
                ,children:[{
                   tag:'span'
                  ,id:'metadata-urlshortcut-title'
                  ,cls:'metadata-urlshortcut'
                  ,html:_('group.sri.urlshortcut.title')
                },{
                   tag:'img'
                  ,id:'metadata-urlshortcut-info'
                  ,cls:'metadata-tooltip'
                  ,src:Curriki.ui.InfoImg
                  ,qtip:_('group.sri.urlshortcut.tooltip')
                }]
              }
            },{
               xtype:'textfield'
              ,id:'metadata-urlshortcut-entry'
              ,name:'urlshortcut'
              ,emptyText:_('group.sri.urlshortcut.empty')
              ,allowBlank:true
              ,preventMark:true
              ,hideLabel:true
              ,width:'80%'
              ,value:Curriki.group.current.sri?Curriki.group.current.sri.urlshortcut:''}]
            }]
        });

        AddPath.GroupMetadataEdit1.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGME1', AddPath.GroupMetadataEdit1);


    AddPath.GroupMetadataEdit2 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part2.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME1');                  
                  }
                  ,scope:this
                }                  
              }
            },{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME3');                    
                  }
                  ,scope:this
                }
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME1');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'2'}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'3', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME3');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'4', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME4');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'5', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME5');
                    }
                    ,scope:this
                  }
                }}               
                ]                
            },'->',{
               text:_('add.setrequiredinfo.publish.button')
              ,id:'publishbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadataEditFinished();                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
    // Education System
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-education_system'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-education_system-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.education_system.title')
                    },{
                       tag:'img'
                      ,id:'metadata-education_system-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.education_system.tooltip')
                    }]
                  }
                },{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.education_system.txt')
                    ,cls:'directions'
                  }
                },{
                   xtype:'combo'
                  ,id:'metadata-education_system-entry'
                  ,hiddenName:'education_system'
                  ,hideLabel:true
                  ,width:'60%'
                  ,mode:'local'
                  ,store:Curriki.data.education_system.store
                  ,displayField:'education_system'
                  ,valueField:'id'
                  ,typeAhead:true
                  ,triggerAction:'all'
                  ,emptyText:_('group.sri.education_system.empty')
                  ,selectOnFocus:true
                  ,forceSelection:true
                  ,value:Curriki.group.current.sri.education_system?Curriki.group.current.sri.education_system
                    :Curriki.data.education_system.initial?Curriki.data.education_system.initial:undefined
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGME2').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.education_system)){
                            Ext.getCmp('metadata-education_system-entry').setValue(md.education_system);
                          }
                        }
                      })
                    }
                  }
                }]

    // Language
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-language'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-language-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.language.title')
                    },{
                       tag:'img'
                      ,id:'metadata-language-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.language.tooltip')
                    }]
                  }
                },{
                   xtype:'combo'
                  ,id:'metadata-language-entry'
                  ,hiddenName:'language'
                  ,hideLabel:true
                  ,width:'60%'
                  ,mode:'local'
                  ,store:Curriki.data.language.store
                  ,displayField:'language'
                  ,valueField:'id'
                  ,typeAhead:true
                  ,triggerAction:'all'
                  ,emptyText:_('group.sri.language.empty')
                  ,selectOnFocus:true
                  ,forceSelection:true
                  ,value:Curriki.group.current.sri.language?Curriki.group.current.sri.language
                    :Curriki.data.language.initial?Curriki.data.language.initial:undefined
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGME2').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.language)){
                            Ext.getCmp('metadata-language-entry').setValue(md.language);
                          }
                        }
                      })
                    }
                  }
                }]
              }]
            }]
        });

        AddPath.GroupMetadataEdit2.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGME2', AddPath.GroupMetadataEdit2);
    
    AddPath.GroupMetadataEdit3 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part3.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME2'); 
                }
                ,scope:this
                }                  
              }
            },{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME4');                    
                  }
                  ,scope:this
                }
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME1'); 
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'2', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME2');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'3'}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'4', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME4');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'5', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME5');
                    }
                    ,scope:this
                  }
                }}              
                ]                
            },'->',{
               text:_('add.setrequiredinfo.publish.button')
              ,id:'publishbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadataEditFinished();                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
    // Educational Level
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.educational_level.txt')
                    ,cls:'directions'
                  }
                },{
                  // A "TreeCheckBoxGroup" would be nice here
                   xtype:'numberfield'
                  ,id:'educational_level-validation'
                  ,allowBlank:false
                  ,preventMark:true
                  ,minValue:1
                  ,hidden:true
                  ,listeners:{
                     valid:function(field){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('el-tree');
                      fieldset.removeClass('x-form-invalid');
                      fieldset.el.dom.qtip = '';
                    }
                    ,invalid:function(field, msg){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('el-tree');
                      fieldset.addClass('x-form-invalid');
                      var iMsg = field.invalidText;
                      fieldset.el.dom.qtip = iMsg;
                      fieldset.el.dom.qclass = 'x-form-invalid-tip';
                      if(Ext.QuickTips){ // fix for floating editors interacting with DND
                        Ext.QuickTips.enable();
                      }

                    }
                  }
                }
                ,(function(){
                  var checkedCount = 0;
                  var md = Curriki.group.current.metadata;
                  if (md) {
                    var el = md.educational_level;
                    Ext.isArray(el) && (function(ca){
                      var childrenFn = arguments.callee;
                      Ext.each(ca, function(c){
                        if (c.id) {
                          if (c.checked = (el.indexOf(c.id) !== -1)) {
                            checkedCount++;
                          }
                          if (c.children) {
                            childrenFn(c.children);
                          }
                        }
                      });
                    })(Curriki.data.el.elChildren);
                  }
                  return Ext.apply(AddPath.elTree = Curriki.ui.component.asset.getElTree(Curriki.group.current.sri.education_system), {
                    listeners: {
                      render:function(comp){
                        comp.findParentByType('apGME3').on('show', function() {
                          Ext.getCmp('educational_level-validation').setValue(checkedCount)
                        });                       
                      }
                      ,resize: function(comp) {
                        var ct = comp.findParentByType('apGME3');
                        ct.syncSize();
                      }
                    }
                  })
                })()] 
              }]
            }]   
        });

        AddPath.GroupMetadataEdit3.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGME3', AddPath.GroupMetadataEdit3);
    
    AddPath.GroupMetadataEdit4 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part4.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,autoScroll:false
          ,autoHeight:true
          ,resizable:false
          ,listeners: {
            resize: function(ct) {
              console.log('window resize');
              var cmp = ct;
              }
            ,bodyresize: function(ct) {
              console.log('window bodyresize');
              var cmp = ct;
              }
          }
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,listeners: {
              resize: function(ct) {
                console.log("form resize");
                var cmp = ct.ownerCt;
                }                     
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME3'); 
                }
                ,scope:this
                }                  
              }
            },'->',{
               text:_('add.setrequiredinfo.next.button')
              ,id:'nextbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME5');                    
                  }
                  ,scope:this
                }
              }
            },{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME1');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'2', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME2');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'3', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME3');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'4'}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'5', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME5');
                    }
                    ,scope:this
                  }
                }}               
                ]                
            },'->',{
               text:_('add.setrequiredinfo.publish.button')
              ,id:'publishbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadataEditFinished();                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            /*
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    //if (height === 'auto') {
                      //fPanel.setHeight('auto');
                   // } else {
                   //   fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    //}
                  }
                );
              }              
            }*/
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.fw_items_txt')
                    ,cls:'directions'
                  }
                },{
                  // A "TreeCheckBoxGroup" would be nice here
                   xtype:'numberfield'
                  ,id:'fw_items-validation'
                  ,allowBlank:false
                  ,preventMark:true
                  ,minValue:1
                  ,hidden:true
                  ,listeners:{
                     valid:function(field){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('fw_items-tree');
                      fieldset.removeClass('x-form-invalid');
                      fieldset.el.dom.qtip = '';
                    }
                    ,invalid:function(field, msg){
                      if (!this.rendered || this.preventMark) {
                        return;
                      }
                      var fieldset = Ext.getCmp('fw_items-tree');
                      fieldset.addClass('x-form-invalid');
                      var iMsg = field.invalidText;
                      fieldset.el.dom.qtip = iMsg;
                      fieldset.el.dom.qclass = 'x-form-invalid-tip';
                      if(Ext.QuickTips){ // fix for floating editors interacting with DND
                        Ext.QuickTips.enable();
                      }

                    }
                  }
                }
                ,(function(){
                  var checkedCount = 0;
                  var md = Curriki.group.current.metadata;
                  if (md) {
                    var fw = md.disciplines;
                    Ext.isArray(fw) && (function(ca){
                      var childrenFn = arguments.callee;
                      Ext.each(ca, function(c){
                        if (c.id) {
                          if (c.checked = (fw.indexOf(c.id) !== -1)) {
                            checkedCount++;
                          }
                          if (c.children) {
                            childrenFn(c.children);
                          }
                        }
                      });
                    })(Curriki.data.fw_item.fwChildren);
                  }
                  return Ext.apply(AddPath.fwTree = Curriki.ui.component.asset.getFwTree(Curriki.group.current.sri.educational_level), {
                    listeners: {
                      render:function(comp){
                        comp.findParentByType('apGME4').on('show', function() {
                          Ext.getCmp('fw_items-validation').setValue(checkedCount)
                        });
                      }
                      ,resize: function(comp) {
                        var ct = comp.findParentByType('apGME4');
                        ct.syncSize();
                      }
                    }
                  })
                })()]
            }]   
        });

        AddPath.GroupMetadataEdit4.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGME4', AddPath.GroupMetadataEdit4);               
        
    AddPath.GroupMetadataEdit5 = Ext.extend(Curriki.ui.dialog.Actions, {
        initComponent:function(){
        Ext.apply(this, {
          title:_('group.add.setrequiredinfo.part5.title')
          ,cls:'addpath addpath-metadata resource resource-add'
          ,width:800
          ,items:[{
             xtype:'form'
            ,id:'MetadataDialoguePanel'
            ,formId:'MetadataDialogueForm'
            ,labelWidth:25
            ,autoHeight:true
            ,autoWidth:true
            ,autoScroll:false
            ,border:false
            ,defaults:{
               labelSeparator:''
            }
            ,bbar:{
              xtype:'toolbar'
              ,layout:'xtoolbar'
              ,items:[{
               text:_('add.setrequiredinfo.previous.button')
              ,id:'previousbutton'
              ,cls:'button button-previous mgn-rt'
              ,listeners:{
                click:{
                  fn: function(e, ev){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadata.Show('apGME4'); 
                }
                ,scope:this
                }                  
              }
            },'->',{
              xtype: 'tbprogress'
              ,items: [
                {tag:'a', cls:'addpath-page circle addpath-page-previous', html:'1', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME1');                      
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'2', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME2');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'3', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME3');
                    }
                    ,scope:this
                  }
                }}
                ,{tag:'a', cls:'addpath-page circle addpath-page-previous', html:'4', listeners:{
                  click: {
                    fn: function(e, ev) {
                      AddPath.GroupMetadata.SetMetadata(this);
                      this.close();
                      AddPath.GroupMetadata.Show('apGME4');
                    }
                    ,scope:this
                  }
                }}                
                ,{tag:'a', cls:'addpath-page circle addpath-page-current', html:'5'}
                ]                
            },'->',{
               text:_('add.setrequiredinfo.publish.button')
              ,id:'publishbutton'
              ,cls:'button button-confirm'
              ,listeners:{
                click:{
                   fn: function(){
                    AddPath.GroupMetadata.SetMetadata(this);
                    this.close();
                    AddPath.GroupMetadataEditFinished();                    
                  }
                  ,scope:this
                }
              }
            }]
            }
            ,monitorValid:true
            ,listeners:{
              render:function(fPanel){
  //TODO: Try to generalize this (for different # of panels)
                fPanel.ownerCt.on(
                  'bodyresize'
                  ,function(wPanel, width, height){
                    if (height === 'auto') {
                      fPanel.setHeight('auto');
                    } else {
                      fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
                    }
                  }
                );
              }
            }
            ,items:[{
// Access level              
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-access_level'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-access_level-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.access_level.title')
                    },{
                       tag:'img'
                      ,id:'metadata-access_level-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.access_level.tooltip')
                    }]
                  }
                },{
                   border:false
                  ,xtype:'radiogroup'
                  ,width:588
                  ,columns:[.95]
                  ,vertical:true
                  ,defaults:{
                    name:'access_level'
                  }
                  ,items:Curriki.data.access_level.radios
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGME5').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.access_level)){
                            Ext.getCmp(Ext.select('input[type="radio"][name="access_level"][value="'+md.access_level+'"]').first().dom.id).setValue(md.access_level);
                          }
                        }
                      })
                    }
                  }
                }]
// Policy                
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-policy'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-policy-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.policy.title')
                    },{
                       tag:'img'
                      ,id:'metadata-policy-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.policy.tooltip')
                    }]
                  }
                },{
                   border:false
                  ,xtype:'radiogroup'
                  ,width:588
                  ,columns:[.95]
                  ,vertical:true
                  ,defaults:{
                    name:'policy'
                  }
                  ,items:Curriki.data.policy.radios
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGME5').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.access_level)){
                            Ext.getCmp(Ext.select('input[type="radio"][name="policy"][value="'+md.policy+'"]').first().dom.id).setValue(md.policy);
                          }
                        }
                      })
                    }
                  }
                }]                           
    // License Deed
              },{
                 border:false
                ,items:[{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,id:'metadata-license'
                    ,cls:'information-header'
                    ,children:[{
                       tag:'span'
                      ,id:'metadata-license-title'
                      ,cls:'metadata-title'
                      ,html:_('group.sri.license.title')
                    },{
                       tag:'img'
                      ,id:'metadata-license-info'
                      ,cls:'metadata-tooltip'
                      ,src:Curriki.ui.InfoImg
                      ,qtip:_('group.sri.license.tooltip')
                    }]
                  }
                },{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:__('group.sri.license.txt')
                    ,cls:'directions'
                  }
                },{
                   xtype:'box'
                  ,autoEl:{
                     tag:'div'
                    ,html:_('groul.sri.license.heading')
                  }
                },{
                   xtype:'combo'
                  ,id:'metadata-license-entry'
                  ,hiddenName:'license'
                  ,hideLabel:true
                  ,width:460
                  ,mode:'local'
                  ,store:Curriki.data.license.store
                  ,displayField:'license'
                  ,valueField:'id'
                  ,typeAhead:true
                  ,triggerAction:'all'
                  ,emptyText:_('group.sri.license.empty')
                  ,selectOnFocus:true
                  ,forceSelection:true
                  ,value:Curriki.data.license.initial
                    ?Curriki.data.license.initial
                    :undefined
                  ,listeners:{
                    render:function(comp){
                      comp.findParentByType('apGME5').on('show', function() {
                        if (!Ext.isEmpty(Curriki.group.current.metadata)) {
                          var md = Curriki.group.current.metadata;

                          if (!Ext.isEmpty(md.licenseType)){
                            Ext.getCmp('metadata-license-entry').setValue(md.licenseType);
                          }
                        }
                      })
                    }
                  }
                }]                
            }]
          }]
        });

        AddPath.GroupMetadataEdit5.superclass.initComponent.call(this);
      }
    });
    Ext.reg('apGME5', AddPath.GroupMetadataEdit5);
    
    AddPath.GroupMetadataFinished = function(){     

      var metadata = Curriki.group.current.sri;
      
      Curriki.groups.CreateGroup(metadata.title, function(info){
        Curriki.group.current.info = info;
        Curriki.groups.SetMetadata(Curriki.group.current.info.groupName
          ,metadata
          ,function(newMetadata){
            console.log("Set group metadata: ", newMetadata);
            AddPath.group.ShowDone();                            
          });  
      });      
    }
    
    AddPath.GroupMetadataEditFinished = function(){     

      var metadata = Curriki.group.current.sri;
      
      Curriki.groups.GetGroupInfo(Curriki.group.current.groupName, function(info){
        Curriki.group.current.info = info;
        Curriki.groups.SetMetadata(Curriki.group.current.groupName
          ,metadata
          ,function(newMetadata){
            console.log("Set group metadata: ", newMetadata);
            AddPath.group.ShowDone();                            
          });  
      });      
    }
}

Ext.ns('Curriki.module.addpath.group');
Curriki.module.addpath.group.startPath = function(path, options){
  Curriki.module.addpath.group.initAndStart(function(){
    Curriki.module.addpath.group.start(path);
  }, options);
}

Curriki.module.addpath.group.startDoneMessage = function(options){
  Curriki.module.addpath.group.initAndStart(function(){
    Curriki.module.addpath.ShowDone();
  }, options);
}

Curriki.module.addpath.group.initAndStart = function(fcn, options){
  
  var current = Curriki.group.current;
  if (!Ext.isEmpty(options)){
    
    current.groupName = options.groupName||current.groupName;
    current.cameFrom = options.cameFrom||current.cameFrom;
    current.groupTitle = options.groupTitle||current.groupTitle;     
  }

  Curriki.init(function(){
    if (Ext.isEmpty(Curriki.data.user.me) || 'XWiki.XWikiGuest' === Curriki.data.user.me.username){
      window.location.href='/xwiki/bin/login/XWiki/XWikiLogin?xredirect='+window.location.href;
      return;
    }

    Curriki.module.addpath.init();

    var startFn = function(){
      fcn();
    }    

    var currentFn;
    if (!Ext.isEmpty(current.groupName)
        && (Ext.isEmpty(current.groupTitle))) {
      // Get group info
      currentFn = function(){
        Curriki.groups.GetGroupInfo(current.groupName, function(info){
          Curriki.current.groupTitle = info.displayTitle;          
          startFn();
        });
      }
    } else {
      currentFn = function(){
        startFn();
      };
    }

    currentFn();
  });
}

Curriki.module.addpath.group.start = function(path){

  console.log('Starting path: ', path);

  // This should already have been handled, but do a simple test here
  if (Ext.isEmpty(Curriki.data.user.me) || 'XWiki.XWikiGuest' === Curriki.data.user.me.username){
    console.log('Not signed in:');
    window.location.href='/xwiki/bin/login/XWiki/XWikiLogin?xredirect='+window.location.href;
    return;
  }

  // Defaults
  if (Ext.isEmpty(Curriki.group.current.cameFrom)) {
    Curriki.group.current.cameFrom = window.location.href;
  }

  if (!Ext.isEmpty(path)) {
    Curriki.group.current.flow = path;
  }

  var pathParts = window.location.pathname.split('/');
  var pathSize = pathParts.size();
  Curriki.group.current.subPath = "";
  for (i = pathSize-2; i < pathSize; i++){
    Curriki.group.current.subPath += "/"+pathParts[i];
  }
  Curriki.logView('/features/resources/add/'+Curriki.group.current.flow+Curriki.group.current.subPath);

  var next = null;
  switch (Curriki.group.current.flow){
    case 'Add':
      next = 'apGM1';      
      Curriki.module.addpath.ShowNextDialogue(next);         
      return;
      break;
    case 'Metadata':
      next = 'apGME1';
      Curriki.groups.GetGroupInfo(Curriki.group.current.groupName, function(info){
        Curriki.group.current.info = info;
        Curriki.groups.GetMetadata(Curriki.group.current.groupName, function(metadata){
          Curriki.group.current.metadata = metadata;
          Curriki.group.current.sri = metadata;
          Curriki.module.addpath.ShowNextDialogue(next);
        });
      })          
      return;
      break;
    
  }
}