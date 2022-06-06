/*
 * Copyright (c) ReichTUM 2022.
 */

package com.adveisor.g2.monopoly.engine.service;

import com.adveisor.g2.monopoly.engine.service.model.Dice;
import com.adveisor.g2.monopoly.engine.service.model.PlayerBid;
import com.adveisor.g2.monopoly.engine.service.model.board.Field;
import com.adveisor.g2.monopoly.engine.service.model.Game;
import com.adveisor.g2.monopoly.engine.service.model.deck.Card;
import com.adveisor.g2.monopoly.engine.service.model.player.Player;
import com.adveisor.g2.monopoly.engine.service.status.*;
import com.adveisor.g2.monopoly.util.Logger;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Service
public class GameService {
    private final Game game;

    // all the possible statuses initialized here
    private AbstractStatus auctionStatus = new AuctionStatus(this);
    private AbstractStatus diceStatus = new DiceStatus(this);
    private AbstractStatus turnStatus = new TurnStatus(this);
    private AbstractStatus endStatus = new EndStatus(this);
    private AbstractStatus waitingStatus = new WaitingStatus(this);
    //

    private AbstractStatus currentStatus;

    @Autowired
    public GameService(Game game) {
        this.game = game;
        // game start at this status
        this.currentStatus = this.waitingStatus;
    }

    public void resetGame() {
        game.resetGame();
    }
    public void setNextPlayer() {
        game.setNextPlayer();
    }

    public void incrementGameVersionId() {
        this.game.incrementVersionId();
    }

    public Long getGameVersionId() {
        return this.game.getVersionId();
    }

    public int getPlayerCount() {
        return this.game.getPlayers().size();
    }

    public List<Player> getPlayers() {
        return this.game.getPlayers();
    }

    public Player getCurrentPlayer() {
        return game.findCurrentPlayer();
    }

    public String getCurrentPlayerId() {

        return game.getCurrentPlayerId();
    }
    // util above to get game info

    /**
     * The method is used to validate if the player trying to make action is the current player
     * if not, the access will be blocked with HTTP 401 Unauthorized
     * @param player a player object of which only the playId field must be specified for validation
     */
    public Player validateActivePlayer(Player player) {
        if (!Objects.equals(player.getPlayerId(), getCurrentPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Player validation failed, non active player cannot take action");
        }
        return getCurrentPlayer();
    }

    // player action below

    /**
     * When the game is in waiting status, new player can attempt to join
     * returned is a player that has been initialized
     * @param player a player object of which only the playerName and piece field is required
     * @return  an initialized player that has a generated playerId and all the initial data determined by the game
     */
    public Player join(Player player) {
        incrementGameVersionId();
        return currentStatus.join(player);
    }

    /**
     * Put the game from waiting status to start.
     * Will only take effect in waiting status, otherwise exception is thrown
     */
    public void start(){
        incrementGameVersionId();
        currentStatus.start();
    }

    /**
     * @return a Field object describing the field that the current player is standing on
     */
    public Field currentPlayerStandingField() {
        incrementGameVersionId();
        return getCurrentPlayer().standingOnField();
    }

    /**
     *
     * @param player The player who is intending to take a card
     * @return a Card object representing the card taken
     */
    public Card takeCard(Player player) {
        incrementGameVersionId();
        return currentStatus.takeCard(validateActivePlayer(player));
    }

    /**
     * Put the game in end status
     */
    public void end(){
        incrementGameVersionId();
        currentStatus = endStatus;
    }

    /**
     * A dice-throw and move according to the throw result.
     * only possible when game is in dice status
     * @param dice a `Dice` object representing the result of the two dice.
     *             if it is not present(null passed), a simulated dice will be used
     * @return a complete `Dice` object containing boolean field `pasch` to indicate whether the throw is a double
     */
    public Dice diceThrow(Dice dice) {
        incrementGameVersionId();
        var toBeReturned = currentStatus.diceThrow(dice);
        Logger.log(this);
        return toBeReturned;
    }

    /**
     * End the current player's round
     * @param player playerId required for validation
     */
    public void continueGame(Player player) {
        incrementGameVersionId();
        validateActivePlayer(player);
        currentStatus.continueGame();
    }

    /**
     * use jail card to get out of jail immediately.
     * throw exception if player is not in jail or do not have jail cards.
     * @param player playerId required for validation.
     */
    public void useJailCard(Player player){
        incrementGameVersionId();
        validateActivePlayer(player).useJailCard();
    }

    /**
     * player hands in 50 currency units to get out of jail immediately.
     * throw exception if player is not in jail.
     * @param player playerId required for validation.
     */
    public void buyOutOfJail(Player player) {
        incrementGameVersionId();
        validateActivePlayer(player).buyOutOfJail();
    }

    /**
     * current player buy the field he/she is currently standing on
     * @param player playerId required for validation.
     */
    public Field buyProperty(Player player) {
        incrementGameVersionId();
        return validateActivePlayer(player).buyProperty(currentPlayerStandingField());
    }

    public void sellPropertyToBank(int fieldIndex){
        incrementGameVersionId();
        currentStatus.sellPropertyToBank(fieldIndex);
    }


    /**
     *
     * @param player the player to be bankrupted
     */
    public void bankruptPlayer(Player player){
        incrementGameVersionId();
        game.bankrupt(player);
        if (game.getNumActivePlayers() < 1) {
            this.setCurrentStatus(endStatus);
        }

        for(int i = 0; i<39; i++){
            if(player.getPossession(i)){
                auctionProperty(i);
            }
        }
    }

    public void auctionProperty(int fieldIndex){
        incrementGameVersionId();
        currentStatus = auctionStatus;
        try {
            currentStatus.startAuction(fieldIndex);
        } catch (InterruptedException e) {
            Logger.log("InterruptedException occurred. Auction ended.");
        }
    }

    public void tryHighestBid(PlayerBid playerBid){
        incrementGameVersionId();
        currentStatus.tryHighestBid(playerBid);
    }

    public void startMortgage(int fieldIndex){
        incrementGameVersionId();
        currentStatus.startMortgage(fieldIndex);
    }


    public void endMortgage(int fieldIndex){
        incrementGameVersionId();
        currentStatus.endMortgage(fieldIndex);
    }


    public void buyHouse(int fieldIndex){
        incrementGameVersionId();
        currentStatus.buyHouse(fieldIndex);
    }


    public void sellHouse(int fieldIndex){
        incrementGameVersionId();
        currentStatus.sellHouse(fieldIndex);
    }

    public static void transaction(Player paying_pl, Player paid_pl, int diff){
        paying_pl.adjustBalance(-diff);
        paid_pl.adjustBalance(diff);
    }
}
