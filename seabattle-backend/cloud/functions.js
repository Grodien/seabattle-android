const gameModule = require('./game.js');
const messagesModule = require('./messages.js');

Parse.Cloud.define("RenewGameField", async (request) => {
    console.log(`RenewGameField request received`);
    const playerId = request.params.playerId;

    const results = await gameModule.findPlayersGame(playerId);
    const config = await gameModule.getConfig();

    if (results.length > 0) {
        let object = results[0];
        var gamefield = gameModule.createRandomGamefield(config);
        if (object.get('player1') === playerId) {
            object.set("gamefield1", gamefield);
        } else {
            object.set("gamefield2", gamefield);
        }
        object.save();
        messagesModule.createFullUpdateMessage(playerId, gamefield, true, false);
    }
});

Parse.Cloud.define("Ready", async (request) => {
    console.log(`Ready request received`);
    const playerId = request.params.playerId;

    const results = await gameModule.findPlayersGame(playerId);
    const config = await gameModule.getConfig();

    if (results.length > 0) {
        let game = results[0];
        if (game.get('player1') === playerId) {
            game.set("player1ready", true);
            messagesModule.createReadyMessage(game.get('player2'), true, false, false);
        } else {
            game.set("player2ready", true);
            messagesModule.createReadyMessage(game.get('player1'), true, false, false);
        }
        game.save().then((game) => {
                // Success
                if (game.get('player1ready') && game.get('player2ready')) {
                    gameModule.startGame(game);
                }
            }
        );
    }
});

Parse.Cloud.define("PlayerShoot", async (request) => {
    console.log(`PlayerShoot request received`);
    const playerId = request.params.playerId;
    const args = request.params.content.split(';');

    const results = await gameModule.findPlayersGame(playerId);

    if (results.length > 0) {
        let game = results[0];
        gameModule.shoot(game, playerId, Number(args[1]), Number(args[2]));
    }
});