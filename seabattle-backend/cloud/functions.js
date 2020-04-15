const gameModule = require('./game.js');
const messagesModule = require('./messages.js');

Parse.Cloud.define("RenewGameField", async (request) => {
    const playerId = request.params.playerId;

    const results = await gameModule.findPlayersGame(playerId);
    const config = await gameModule.getConfig();

    if (results.length > 0) {
        let object = results[0];
        var gamefield = gameModule.createRandomGamefield(config);
        if (object.get('player1') === playerId) {
            object.set("gamefield1", gamefield);
            object.save();
        } else {
            object.set("gamefield2", gamefield);
            object.save();
        }
        messagesModule.createFullUpdateMessage(playerId, gamefield, "1", "0");
    }
});