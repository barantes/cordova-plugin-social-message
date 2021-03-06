var exec = require("cordova/exec");

var SocialMessage = function () {
    this.name = "SocialMessage";
};

var allActivityTypes = ["PostToFacebook", "PostToTwitter", "PostToWeibo", "Message", "Mail", "Print", "CopyToPasteboard", "AssignToContact", "SaveToCameraRoll", "AddToReadingList", "PostToFlickr", "PostToVimeo", "TencentWeibo", "AirDrop"];

SocialMessage.prototype.send = function (message) {
    if (!message) {
        return;
    }
    if (typeof (message.activityTypes) === "undefined" || message.activityTypes === null || message.activityTypes.length === 0) {
        message.activityTypes = allActivityTypes;
    }
    message.activityTypes = message.activityTypes.join(",");
    exec(null, null, "SocialMessage", "send", [message]);
};

/** Opens a profile or page using either Facebook native app (if installed) or Facebook's website on external browser.
 * example: var options = {facebookId : "123456"};
**/
SocialMessage.prototype.openFacebook = function (options) {
		exec(null, null, "SocialMessage", "openFacebook", [options]);
	};

module.exports = new SocialMessage();
