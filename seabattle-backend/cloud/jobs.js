Parse.Cloud.job("CleanUp", (request) =>  {
    // params: passed in the job call
    // headers: from the request that triggered the job
    // log: the ParseServer logger passed in the request
    // message: a function to update the status message of the job object
    const { params, headers, log, message } = request;
    message("Cleaning up database...");

    deleteAllOldMessages(message)
});

function deleteAllOldMessages(message) {

    var date = new Date();
    date.setHours(0, 0, 0, 0);

    const query = new Parse.Query("Messages");
    query.lessThan('createdAt', date);
    query.find().then(function(results) {
        Parse.Object.destroyAll(results)
        if (results.length === 100) {
            deleteAllOldMessages();
        } else {
            message("Finshed cleaning up Messages");
        }
    });
}