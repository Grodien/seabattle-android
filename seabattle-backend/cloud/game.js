const messagesModule = require('./messages.js');

const VALUE_FREE = '0';
const VALUE_SHIP = '1';
const VALUE_FREE_HIT = '2';
const VALUE_SHIP_HIT = '3';

const SIZE = 10;
const ULTIMATE_LENGTH = 8;
const HUGE_LENGTH = 4;
const BIG_LENGTH = 3;
const MEDIUM_LENGTH = 2;
const SMALL_LENGTH = 1;

const SHIP_PLACEMENT_MAX_TRYS = 200;
const RANDOM_PLACEMENT_MAX_TRYS = 10;

const NEIGHBORS =
    [[-1, -1],
        [-1, 0],
        [-1, 1],
        [0, -1],
        [0, 1],
        [1, -1],
        [1, 0],
        [1, 1]];


module.exports.createGame = async function createGame(p1, p2) {
    console.log(`Creating Game`);

    const config = await getConfig();
    if (config === null) {
        console.log(`Can't create game without a config...`)
    } else {
        messagesModule.createPlayerFoundMessage(p2.get('playerId'), p1.get('playerName'));
        messagesModule.createPlayerFoundMessage(p1.get('playerId'), p2.get('playerName'));

        const Game = Parse.Object.extend("Game");
        const entry = new Game();
        entry.set("player1", p1.get('playerId'));
        entry.set("player2", p2.get('playerId'));
        entry.set("gameSize", config.get('size'));
        entry.set("gamefield1", createRandomGamefield(config));
        entry.set("gamefield2", createRandomGamefield(config));
        entry.set("player1ready", false);
        entry.set("player2ready", false);
        entry.set("gamestate", 0);
        entry.save();

        messagesModule.createFullUpdateMessage(p1.get('playerId'), entry.get('gamefield1'), true, false);
        messagesModule.createFullUpdateMessage(p2.get('playerId'), entry.get('gamefield2'), true, false);
    }
};

async function getConfig() {
    const configQuery = new Parse.Query("Config");
    const results = await configQuery.find();

    if (results.length > 0) {
        return results[0];
    } else {
        console.log(`Missing game configuration!`);
        return null;
    }
};
module.exports.getConfig = getConfig;

function createRandomGamefield(config) {
    console.log(`Creating gamefield`);
    const size = parseInt(config.get('size'));
    const ultimate = parseInt(config.get('ultimateShips'));
    const huge = parseInt(config.get('hugeShips'));
    const big = parseInt(config.get('bigShips'));
    const medium = parseInt(config.get('mediumShips'));
    const small = parseInt(config.get('smallShips'));

    var builder = [];
    for (let i = 0; i < size; i++) {
        for (let j = 0; j < size; j++) {
            builder.push(VALUE_FREE);
        }
    }

    let count = 0;
    while (count < RANDOM_PLACEMENT_MAX_TRYS) {
        count++;

        var gamefield = builder.join("");

        for (let i = 0; i < parseInt(config.get('ultimateShips')); i++) {
            gamefield = fillShipByRandom(gamefield, size, ULTIMATE_LENGTH);
        }

        for (let i = 0; i < parseInt(config.get('hugeShips')); i++) {
            gamefield = fillShipByRandom(gamefield, size, HUGE_LENGTH);
        }

        for (let i = 0; i < parseInt(config.get('bigShips')); i++) {
            gamefield = fillShipByRandom(gamefield, size, BIG_LENGTH);
        }

        for (let i = 0; i < parseInt(config.get('mediumShips')); i++) {
            gamefield = fillShipByRandom(gamefield, size, MEDIUM_LENGTH);
        }

        for (let i = 0; i < parseInt(config.get('smallShips')); i++) {
            gamefield = fillShipByRandom(gamefield, size, SMALL_LENGTH);
        }

        if (ultimate * ULTIMATE_LENGTH + huge * HUGE_LENGTH
            + big * BIG_LENGTH + medium * MEDIUM_LENGTH
            + small * SMALL_LENGTH === (gamefield.match(/1/g) || []).length) {
            return gamefield;
        }
    }

    return null
}

module.exports.createRandomGamefield = createRandomGamefield;

function getGamefieldValueAtIndex(gamefield, size, x, y) {
    return gamefield.substr(x * size + y, 1);
}

function replaceGamefieldValueAtIndex(gamefield, size, x, y, value) {
    return gamefield.substr(0, x * size + y) + value + gamefield.substr(x * size + y + 1);
}

function isValidCoord(size, x, y) {
    return x >= 0 && x < size && y >= 0 && y < size;
}

function getRandomInt(max) {
    return Math.floor(Math.random() * Math.floor(max));
}

function fillShipByRandom(gamefield, size, length) {

    var tryCount = 0;
    while (tryCount < SHIP_PLACEMENT_MAX_TRYS) {
        tryCount++;

        // Choose random direction
        var x2 = 0;
        var y2 = 0;
        if (getRandomInt(2) === 0) {
            x2 = 1;
        } else {
            y2 = 1;
        }

        // Choose random position
        var x1 = getRandomInt(size - (x2 * length));
        var y1 = getRandomInt(size - (y2 * length));

        if (canPlaceShipHere(gamefield, size, x1, y1, x1 + x2 * length, y1 + y2 * length)) {
            for (let l = 0; l < length; l++) {
                gamefield = replaceGamefieldValueAtIndex(gamefield, size, x1 + x2 * l, y1 + y2 * l, VALUE_SHIP);
            }
            return gamefield;
        }
    }

    return gamefield;
}

function areNeighborsFree(gamefield, size, x, y) {
    for (let i = 0; i < NEIGHBORS.length; i++) {
        var dX = NEIGHBORS[i][0];
        var dY = NEIGHBORS[i][1];
        if (isValidCoord(size, x + dX, y + dY)) {
            if (getGamefieldValueAtIndex(gamefield, size, x + dX, y + dY) === VALUE_SHIP) {
                return false;
            }
        }
    }
    return true;
}

function canPlaceShipHere(gamefield, size, x1, y1, x2, y2) {
    if (isValidCoord(size, x1, y1) && isValidCoord(size, x2, y2)) {
        for (let i = x1; i <= x2; i++) {
            if (!areNeighborsFree(gamefield, size, i, y1)) {
                return false;
            }
        }
        for (let i = y1; i <= y2; i++) {
            if (!areNeighborsFree(gamefield, size, x1, i)) {
                return false;
            }
        }
    } else {
        return false;
    }
    return true;
}

module.exports.findPlayersGame = async function findPlayersGame(playerId) {
    const p1query = new Parse.Query("Game");
    p1query.equalTo('player1', playerId);
    const p2query = new Parse.Query("Game");
    p2query.equalTo('player2', playerId);

    const query = Parse.Query.or(p1query, p2query);
    query.descending("createdAt");
    return query.find();
}

module.exports.startGame = async function startGame(game) {
    console.log(`Starting Game`);

    game.set('gamestate', 1);
    game.set('activePlayer', game.get('player1'));
    messagesModule.createReadyMessage(game.get('player1'), true, true, true);
    messagesModule.createReadyMessage(game.get('player2'), true, true, false);
    game.save();
};

module.exports.shoot = async function shoot(game, playerId, x, y) {

    if (game.get('gamestate') === 1 && game.get('activePlayer') === playerId) {
        var size = Number(game.get('gameSize'));
        var gamefieldKey;
        var thisPlayerKey
        var otherPlayerKey;
        if (playerId === game.get('player1')) {
            otherPlayerKey = 'player2';
            gamefieldKey = 'gamefield2';
            thisPlayerGamefieldKey = 'gamefield1';
        } else {
            otherPlayerKey = 'player1';
            gamefieldKey = 'gamefield1';
            thisPlayerGamefieldKey = 'gamefield2';
        }
        var gamefield = game.get(gamefieldKey);

        var cell = getGamefieldValueAtIndex(gamefield, size, x, y);
        if (cell === VALUE_FREE) {
            console.log(`Empty cell hit at x=${x}, y=${y}`);
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y, VALUE_FREE_HIT);
            game.set(gamefieldKey, gamefield);
            game.set('activePlayer', game.get(otherPlayerKey));
            game.save().then((game) => {
                    messagesModule.createPartialUpdateMessage(playerId, x, y, VALUE_FREE_HIT, false, false);
                    messagesModule.createPartialUpdateMessage(game.get(otherPlayerKey), x, y, VALUE_FREE_HIT, true, true);
                }
            );
        } else if (cell === VALUE_SHIP) {
            console.log(`Ship cell hit at x=${x}, y=${y}`);
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y, VALUE_SHIP_HIT);
            if (isShipDestroyed(gamefield, size, x, y)) {
                gamefield = destroyShipSurroundings(gamefield, size, x, y);
                game.set(gamefieldKey, gamefield);
                if (hasMoreShips(gamefield)) {
                    game.save().then((game) => {
                            messagesModule.createFullUpdateMessage(playerId, gamefield.replace(/1/g, VALUE_FREE), false, true);
                            messagesModule.createFullUpdateMessage(game.get(otherPlayerKey), gamefield, true, false);
                        }
                    );
                } else {
                    console.log(`Game over -> player ${playerId} wins`);
                    game.set('gamestate', 2);
                    game.save().then((game) => {
                            addWin(playerId);
                            addLose(game.get(otherPlayerKey));
                            messagesModule.createFullUpdateMessage(playerId, gamefield, false, false);
                            messagesModule.createFullUpdateMessage(game.get(otherPlayerKey), gamefield, true, false);
                            messagesModule.createFullUpdateMessage(game.get(otherPlayerKey), game.get(thisPlayerGamefieldKey), false, false);

                            messagesModule.createWinMessage(playerId, true);
                            messagesModule.createWinMessage(game.get(otherPlayerKey), false);
                        }
                    );
                }
            } else {
                game.set(gamefieldKey, gamefield);
                game.save().then((game) => {
                        messagesModule.createPartialUpdateMessage(playerId, x, y, VALUE_SHIP_HIT, false, true);
                        messagesModule.createPartialUpdateMessage(game.get(otherPlayerKey), x, y, VALUE_SHIP_HIT, true, false);
                    }
                );
            }
        } else {
            console.log(`Already shot at x=${x}, y=${y}`);
        }
    } else {
        console.log(`Failed to shoot! Gamestate=${game.get('gamestate')}, activePlayer=${game.get('activePlayer')}, playerId=${playerId}`);
    }
};

async function addWin(playerId) {
    console.log(`Add win to player=${playerId}`);
    const playerQuery = new Parse.Query("Players");
    playerQuery.equalTo('playerId', playerId);
    const playerResults = await playerQuery.find();

    if (playerResults.length > 0) {
        var player = playerResults[0];
        player.set('wins', Number(player.get('wins')) + 1);
        console.log(`total wins=${player.get('wins')}`);
        player.save();
    }
}

async function addLose(playerId) {
    console.log(`Add lose to player=${playerId}`);
    const playerQuery = new Parse.Query("Players");
    playerQuery.equalTo('playerId', playerId);
    const playerResults = await playerQuery.find();

    if (playerResults.length > 0) {
        var player = playerResults[0];
        player.set('loses', Number(player.get('loses')) + 1);
        console.log(`total loses=${player.get('loses')}`);
        player.save();
    }
}

function isShipDestroyed(gamefield, size, x, y) {
    while ((isValidCoord(size, x + 1, y))
    && (getGamefieldValueAtIndex(gamefield, size, x + 1, y) != VALUE_FREE)
    && (getGamefieldValueAtIndex(gamefield, size, x + 1, y) != VALUE_FREE_HIT)) {
        x++;
        if (getGamefieldValueAtIndex(gamefield, size, x, y) == VALUE_SHIP) {
            return false;
        }
    }
    while ((isValidCoord(size, x - 1, y))
    && (getGamefieldValueAtIndex(gamefield, size, x - 1, y) != VALUE_FREE)
    && (getGamefieldValueAtIndex(gamefield, size, x - 1, y) != VALUE_FREE_HIT)) {
        x--;
        if (getGamefieldValueAtIndex(gamefield, size, x, y) == VALUE_SHIP) {
            return false;
        }
    }
    while ((isValidCoord(size, x, y - 1))
    && (getGamefieldValueAtIndex(gamefield, size, x, y - 1) != VALUE_FREE)
    && (getGamefieldValueAtIndex(gamefield, size, x, y - 1) != VALUE_FREE_HIT)) {
        y--;
        if (getGamefieldValueAtIndex(gamefield, size, x, y) == VALUE_SHIP) {
            return false;
        }
    }
    while ((isValidCoord(size, x, y + 1))
    && (getGamefieldValueAtIndex(gamefield, size, x, y + 1) != VALUE_FREE)
    && (getGamefieldValueAtIndex(gamefield, size, x, y + 1) != VALUE_FREE_HIT)) {
        y++;
        if (getGamefieldValueAtIndex(gamefield, size, x, y) == VALUE_SHIP) {
            return false;
        }
    }
    return true;
}

function destroyShipSurroundings(gamefield, size, x, y) {

    while (isValidCoord(size, x + 1, y) && getGamefieldValueAtIndex(gamefield, size, x + 1, y) == VALUE_SHIP_HIT) {
        if (isValidCoord(size, x, y + 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y + 1, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x, y - 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y - 1, VALUE_FREE_HIT);
        }
        x++;
        if (isValidCoord(size, x, y + 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y + 1, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x, y - 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y - 1, VALUE_FREE_HIT);
        }
    }
    if (isValidCoord(size, x + 1, y + 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y + 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x + 1, y - 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y - 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x + 1, y)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y, VALUE_FREE_HIT);
    }
    while (isValidCoord(size, x - 1, y) && getGamefieldValueAtIndex(gamefield, size, x - 1, y) == VALUE_SHIP_HIT) {
        if (isValidCoord(size, x, y + 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y + 1, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x, y - 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y - 1, VALUE_FREE_HIT);
        }
        x--;
        if (isValidCoord(size, x, y + 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y + 1, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x, y - 1)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y - 1, VALUE_FREE_HIT);
        }
    }
    if (isValidCoord(size, x - 1, y + 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y + 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x - 1, y - 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y - 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x - 1, y)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y, VALUE_FREE_HIT);
    }
    while (isValidCoord(size, x, y + 1) && getGamefieldValueAtIndex(gamefield, size, x, y + 1) == VALUE_SHIP_HIT) {
        if (isValidCoord(size, x + 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x - 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y, VALUE_FREE_HIT);
        }
        y++;
        if (isValidCoord(size, x + 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x - 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y, VALUE_FREE_HIT);
        }
    }
    if (isValidCoord(size, x + 1, y + 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y + 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x - 1, y + 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y + 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x, y + 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y + 1, VALUE_FREE_HIT);
    }
    while (isValidCoord(size, x, y - 1) && getGamefieldValueAtIndex(gamefield, size, x, y - 1) == VALUE_SHIP_HIT) {
        if (isValidCoord(size, x + 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x - 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y, VALUE_FREE_HIT);
        }
        y--;
        if (isValidCoord(size, x + 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y, VALUE_FREE_HIT);
        }
        if (isValidCoord(size, x - 1, y)) {
            gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y, VALUE_FREE_HIT);
        }
    }
    if (isValidCoord(size, x + 1, y - 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x + 1, y - 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x - 1, y - 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x - 1, y - 1, VALUE_FREE_HIT);
    }
    if (isValidCoord(size, x, y - 1)) {
        gamefield = replaceGamefieldValueAtIndex(gamefield, size, x, y - 1, VALUE_FREE_HIT);
    }

    return gamefield;
}

function hasMoreShips(gamefield) {
    return gamefield.includes(VALUE_SHIP);
}



