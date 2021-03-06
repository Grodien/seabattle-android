module.exports.createFullUpdateMessage = async function createFullUpdateMessage(playerId, gamefield, myField, myTurn) {
    const Messages = Parse.Object.extend("Messages");
    const message = new Messages();
    message.set("playerId", playerId);
    message.set("content", "1;" + gamefield + ";" + myField + ";" + myTurn);
    message.save();
};

module.exports.createPlayerFoundMessage = async function createPlayerFoundMessage(playerId, otherPlayerName) {
    const Messages = Parse.Object.extend("Messages");
    const message = new Messages();
    message.set("playerId", playerId);
    message.set("content", "6;" + otherPlayerName);
    message.save();
};

module.exports.createGameSettingsMessage = async function createGameSettingsMessage(playerId) {
    const configQuery = new Parse.Query("Config");
    const results = await configQuery.find();

    if (results.length > 0) {
        let config = results[0];

        const Messages = Parse.Object.extend("Messages");
        const message = new Messages();
        message.set("playerId", playerId);
        message.set("content", "3;" + config.get('size') + ";" + config.get('smallShips') + ";" + config.get('mediumShips')
            + ";" + config.get('bigShips') + ";" + config.get('hugeShips')+ ";" + config.get('ultimateShips'));
        message.save();
    }
};

module.exports.createReadyMessage = async function createReadyMessage(playerId, ready, startGame, myTurn) {
    const Messages = Parse.Object.extend("Messages");
    const message = new Messages();
    message.set("playerId", playerId);
    message.set("content", "2;" + ready + ";" + startGame + ";" + myTurn);
    message.save();
};

module.exports.createPartialUpdateMessage = async function createPartialUpdateMessage(playerId, x, y, value, myField, myTurn) {
    const Messages = Parse.Object.extend("Messages");
    const message = new Messages();
    message.set("playerId", playerId);
    message.set("content", "0;" + x + ";" + y + ";" + value + ";" + myField + ";" + myTurn);
    message.save();
};

module.exports.createWinMessage = async function createWinMessage(playerId, win) {
    const Messages = Parse.Object.extend("Messages");
    const message = new Messages();
    message.set("playerId", playerId);
    message.set("content", "5;" + win);
    message.save();
};