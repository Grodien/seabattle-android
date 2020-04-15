const gameModule = require('./game.js');
const messageModule = require('./messages.js');

Parse.Cloud.define("queue", async (request) => {
    const playerId = request.params.playerId;
    const playerName = request.params.playerName;

    const playerQuery = new Parse.Query("Players");
    playerQuery.equalTo('playerId', playerId);
    const playerResults = await playerQuery.find();

    if (playerResults.length === 0) {
        // Create the new player

        const Players = Parse.Object.extend("Players");
        const player = new Players();
        player.set("playerId", playerId);
        player.set("playerName", playerName);
        player.set("wins", 0);
        player.set("loses", 0);
        player.save()
            .then((player) => {
                // Success
                console.log('New player created with objectId: ' + player.id);
            }, (error) => {
                // Save fails
                console.log('Failed to create new player, with error code: ' + error.message);
            });
    }

    const query = new Parse.Query("Queue");
    query.equalTo('playerId', playerId);
    const results = await query.find();

    if (results.length === 0) {
        const Queue = Parse.Object.extend("Queue");
        const entry = new Queue();
        entry.set("playerId", playerId);
        entry.set("playerName", playerName);
        entry.save().then((player) => {
            // Success
            console.log('player added to queue');
        }, (error) => {
            // Save fails
            console.log('Failed to add player to queue, with error code: ' + error.message);
        });
    }

    messageModule.createGameSettingsMessage(playerId)

    checkEnoughPlayers();
});

async function checkEnoughPlayers() {
    const query = new Parse.Query("Queue");
    const results = await query.find();
    if (results.length > 1) {
        let p1 = results[0];
        let p2 = results[1];

        gameModule.createGame(p1, p2);

        p1.destroy();
        p2.destroy();
    }
}

Parse.Cloud.define("dequeue", async (request) => {
    const playerId = request.params.playerId;

    const query = new Parse.Query("Queue");
    query.equalTo('playerId', playerId);
    const results = await query.find();

    for (let i = 0; i < results.length; i++) {
        let object = results[i];
        object.destroy().then((player) => {
            // The object was deleted from the Parse Cloud.
        }, (error) => {
            console.log(`Error while trying to delete ${object.id}. Message: ${e.message}`);
            // The delete failed.
            // error is a Parse.Error with an error code and message.
        });
    }
});
