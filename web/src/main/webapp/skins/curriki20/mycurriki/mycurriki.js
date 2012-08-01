Ext.ns('Curriki.mycurriki')
Curriki.mycurriki.validateInlineForm = function() {
  var isValid = true;
  if (document.forms.inline['password'].value.indexOf(" ") >= 0) {
    alert(_("profile.field.password.nospaces"));
    isValid = false;
  } 

  if (document.forms.inline['XWiki.XWikiUsers_0_first_name'].value.length == 0) {
    alert(_("profile.field.firstName.mandatory"));
    isValid = false;
  }

  if (document.forms.inline['XWiki.XWikiUsers_0_last_name'].value.length == 0) {
    alert(_("profile.field.lastName.mandatory"));
    isValid = false;
  }

  var emailStr = document.forms.inline['XWiki.XWikiUsers_0_email'].value;
  if (emailStr.length <= 1) {
    alert(_("profile.field.email.mandatory"));
    isValid = false;
  } else {
    var atIndex = emailStr.indexOf("@");
    if ((atIndex < 1) || (emailStr.lastIndexOf(".") <= (atIndex+1)) || (emailStr.length <= (emailStr.lastIndexOf(".") + 1)) || (emailStr.lastIndexOf("@") != atIndex)) {
      alert(_("profile.field.email.invalid"));
      isValid = false;
    }
  }

  if (document.forms.inline['password_repeat'].value != document.forms.inline['password'].value){
    alert(_("profile.field.password.mustMatch"));       
    isValid = false;
  } else {
    if (document.forms.inline['password'].value.length > 0) {
      if (document.forms.inline['password'].value.length < 5) {
        alert("$msg.get("profile.field.password.tooShort")");
        isValid = false;
      } else {
        var pwd=document.createElement('input');
        pwd.setAttribute('id', "XWiki.XWikiUsers_0_password");
        pwd.setAttribute('name', "XWiki.XWikiUsers_0_password");
        pwd.setAttribute('type', "hidden");
        pwd.setAttribute('value', document.forms.inline['password'].value);
        $('inline').appendChild(pwd);
        //document.forms.inline['XWiki.XWikiUsers_0_password'].value = document.forms.inline['password'].value          
      }           
    }
  }
  
  /*
  if (document.forms.inline['origemail'].value != document.forms.inline['XWiki.XWikiUsers_0_email'].value) {
    isValid = validateEmail();
    document.forms.inline.action='$userdoc.getURL("save", "xredirect=$xredirectemail")';
    if (isValid) {
      document.forms.inline.onSubmit = "";    
      var ia=document.createElement('input');
    ia.setAttribute('id', "XWiki.XWikiUsers_0_active");
    ia.setAttribute('name', "XWiki.XWikiUsers_0_active");
    ia.setAttribute('type', "hidden");
    ia.setAttribute('value', "0");
    $('inline').appendChild(ia);

    var eu=document.createElement('input');
    eu.setAttribute('id', "XWiki.XWikiUsers_0_email_undeliverable");
    eu.setAttribute('name', "XWiki.XWikiUsers_0_email_undeliverable");
    eu.setAttribute('type', "hidden");
    eu.setAttribute('value', "1");
    $('inline').appendChild(eu);

    document.forms.inline.action='$userdoc.getURL("save", "xredirect=$xredirectemail2")';    
    document.forms.inline.submit();
    }
  }
  */
  
  return isValid;
}

Curriki.mycurriki.validateEmail = function() {
  var email = $F("XWiki.XWikiUsers_0_email");
  var account = Curriki.mycurriki.username;
  var params = 'xpage=plain&email=' + email + '&account=' + account;
  var valid = false;
  var myAjax = new Ajax.Request('/xwiki/bin/view/XWiki/CheckAccountCreation', {
     method: 'get'
    ,parameters: params
    ,onComplete: function(originalRequest) {
      var text = originalRequest.responseText;
      var res = eval('[' + text + '][0]');
      if (!res.email){
        alert(_("joincurriki.email.alreadyUsed"));
        valid = false;
      }
      valid = true;
    } 
  });
  
  return false;
}

Curriki.mycurriki.validateImgExtension = function(fileFieldName) {
  var isValid = true;
  var ext = getFileExtension(fileFieldName);
  if (ext) {
    if (ext != "ai" && ext != "gif" && ext != "jpg" && ext != "tif" && ext != "bmp" && ext != "jpe" && ext != "psd" && ext != "png" && ext != "jpeg") {
      isValid = false;
    }
  } else {
    isValid = false;
  }
  if (!isValid) {
    alert(_("mycurriki.profile.needPicture"));
  }
  return isValid;
}

Curriki.mycurriki.getFileExtension = function(fileFieldName) {
  var fileName = document.getElementById(fileFieldName).value;
  if (fileName.length){
    fileName = fileName.toLowerCase();
    var pos = fileName.lastIndexOf(".");
    if (pos > 0) {
      return fileName.substring(pos + 1);
    }
  }
  return "";
}
