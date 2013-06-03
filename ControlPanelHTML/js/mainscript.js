/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 28-5-13
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
//
//	jQuery Validate script
//
$(document).ready(function(){

    // Validate
    // http://bassistance.de/jquery-plugins/jquery-plugin-validation/
    // http://docs.jquery.com/Plugins/Validation/
    // http://docs.jquery.com/Plugins/Validation/validate#toptions

    $('#contact-form').validate({
        rules: {
            inputWebPort: {
                minlength: 2,
                maxlength: 5,
                required: true,
                digits: true
            },
            inputControlPort: {
                minlength: 2,
                maxlength: 5,
                required: true,
                digits: true
            },
            inputWebroot: {
                minlength: 2,
                maxlength: 50,
                required: true
            },
            inputDefaultPage: {
                minlength: 2,
                maxlength: 50,
                required: true
            }
        },
        highlight: function(element) {
            $(element).closest('.control-group').removeClass('success').addClass('error');
        },
        success: function(element) {
            element
                .text('OK!').addClass('valid')
                .closest('.control-group').removeClass('error').addClass('success');
        }
    });

}); // end document.ready