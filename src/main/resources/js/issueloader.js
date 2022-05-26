var issueloader = {};


window.onbeforeunload = function() {
    // return 'You have unsaved changes!';
}

issueloader.module = (function () {


    // глобальная переменная модуля хранит урл инстанса
    var jiraBaseUrl = null;

    /////////////////////////////////////////////////////////
    // функция для получения урл из датапровайдера при первом обращении
    var getBaseUrl = function() {

        // инициализируем переменную при первом запуске
        if (jiraBaseUrl == null) {
            // The data key is [groupId].[artifactId]:[web-resource-key].[data-key]
            var JIRA_BASE_URL_DATA_KEY = 'ru.segezhagroup.issueloader.loader:issueloader-resources.web-data';

            // Call WRM.data.claim() to retrieve your data.
            var data_from_provider = WRM.data.claim(JIRA_BASE_URL_DATA_KEY);
            jiraBaseUrl = data_from_provider.home_url;
        }

        return jiraBaseUrl;
    };



    var uploadFile = function() {

        ///////////////////////////////////////////
        // проверка на заполненность полей
        ///////////////////////////////////////////
        // if (findErrorsInFields()) {
        //     return true;
        // }


        ///////////////////////////////////////////

        // на время заблокируем кнопку
        AJS.$("#loader-form .buttons button").attr("disabled", "disabled");
        // AJS.$("#ticket-form .buttons button").removeAttr("disabled");


        var restUrl = AJS.params.baseURL + "/rest/loaderrest/1.0/load/jsonfile";
        // var restUrl = getBaseUrl() + "/rest/loaderrest/1.0/load/jsonfile";



        var formDataTicket = new FormData();

        formDataTicket.append("project-id", AJS.$("#project-id").val());


        var fileObj = AJS.$("#issues-file-upload")[0].files;
        var fileLen =  fileObj.length;
        for (var i = 0; i < fileLen; i++) {
            formDataTicket.append("issues-file-upload", fileObj[i]);

        }


        // var form = AJS.$("#ticket-form")[0];
        // var formDataTicket = new FormData(form);


        AJS.$.ajax({
            url: restUrl,
            type: 'post',
            enctype: 'multipart/form-data',
            processData: false,  // Important!
            dataType: 'json',
            data: formDataTicket,
            cache: false,
            async: true,
            // async: asyncFlag,
            // contentType: "application/json; charset=utf-8",
            contentType: false,
            success: function (data) {

                AJS.$("#loader-error-log").text(data.description);
                console.log(data);


                var msg = "статус: " + data.status + "<br />" + data.description;

                var myFlag = AJS.flag({
                    type: "success",
                    body: '<span style="color: #FF0000;">' + msg +  '</span>'
                });


            },
            error: function (data) {
                // разблокируем кнопку
                AJS.$("#loader-form .buttons button").removeAttr("disabled");

                var myFlag = AJS.flag({
                    type: "error",
                    body: '<span style="color: #FF0000;">Ошибка при отправке !!!</span>'
                });

                AJS.$("#loader-error-log").text(data);
            },
            complete: function () {
                AJS.$("#loader-form .buttons button").removeAttr("disabled");

            },

        });
    };





    return {
        uploadFile:uploadFile,
        // redirectToMenu:redirectToMenu
    };


}());