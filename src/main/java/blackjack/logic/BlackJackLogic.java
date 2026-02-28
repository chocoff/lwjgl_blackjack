package blackjack.logic;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import java.util.ArrayList;
import java.util.Random;

import blackjack.engine.scene.EntityLoader;
import blackjack.engine.scene.Scene;
import blackjack.engine.sound.SoundManager;
import blackjack.engine.Window;
import blackjack.gui.BlackJackGui;

public class BlackJackLogic {
    private static BlackJackLogic instance = new BlackJackLogic();
    private static SoundManager soundManager;

    public static void setSoundManager(SoundManager manager) {
        soundManager = manager;
    }

    public static BlackJackLogic getInstance() {
        return instance;
    }

    public enum GameState {
        NONE,       // Not started
        ROUND_START,
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_OVER,
        GAME_OVER,
    }

    private static GameState state = GameState.ROUND_START;;

    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() { return type + "_" + value; }

        public int getValue() {
            if (value.equals("1")) return 11;
            else if (value.equals("11") || value.equals("12") || value.equals("13")) return 10;
            else return Integer.parseInt(value);
        }

        public boolean isAce() { return value.equals("1"); }

        public String getPath() {
            return toString();
        }
    }

    public static float PlayerCapital = 1000f;
    public static int PlayerBet = 0;

    public static boolean hiddenCardReavaled = false;
    public static int dealerSumNoHiddenCard;

    // Variables of messages
    public static final float Start_X_DecideWinner = 0.5f;
    public static final float Start_Y_DecideWinner = 0.35f; 
    public static final float DecideWinner_Scale = 4.0f;

    public static final float Start_X_PlayerBet = 0.06f;
    public static final float Start_Y_PlayerBet = 0.9f; 
    public static final float PlayerBet_Scale = 3.0f;

    public static final float Start_X_PlayerCapital = 0.06f;
    public static final float Start_Y_PlayerCapital = 0.8f; 
    public static final float PlayerCapital_Scale = 3.0f;

    public static final float Start_X_PlayerPoints = 0.06f;
    public static final float Start_Y_PlayerPoints = 0.2f; 
    public static final float PlayerPoints_Scale = 3.0f;

    public static final float Start_X_DealerPoints = 0.06f;
    public static final float Start_Y_DealerPoints = 0.1f; 
    public static final float DealerPoints_Scale = 3.0f;

    public static final float Start_X_GameOver =  0.5f;
    public static final float Start_Y_GameOver = 0.35f; 
    public static final float GameOver_Scale = 6.0f;

    // Variables of buttons
    public static final float Start_X_ShowCardsButton =  0.80f;
    public static final float Start_Y_ShowCardsButton = 0.88f; 
    public static final float width_ShowCardsButton = 200f;
    public static final float height_ShowCardsButton = 80f;

    public static final float Start_X_HitButton =  0.80f;
    public static final float Start_Y_HitButton = 0.88f; 
    public static final float width_HitButton = 200f;
    public static final float height_HitButton = 80f;

    public static final float Start_X_StandButton = 0.80f;
    public static final float Start_Y_StandButton = 0.78f;
    public static final float width_StandButton   = 200f;
    public static final float height_StandButton  = 80f;

    public static final float Start_X_DoubleButton = 0.80f;
    public static final float Start_Y_DoubleButton = 0.68f;
    public static final float width_DoubleButton   = 200f;
    public static final float height_DoubleButton  = 80f;

    public static final float Start_X_ContinueButton = 0.8f;
    public static final float Start_Y_ContinueButton = 0.78f;
    public static final float width_ContinueButton   = 200f;
    public static final float height_ContinueButton  = 80f;

    public static final float Start_X_NewGameButton = 0.8f;
    public static final float Start_Y_NewGameButton = 0.78f;
    public static final float width_NewGameButton   = 200f;
    public static final float height_NewGameButton  = 80f;

    public static final float Start_X_QuitButton = 0.8f;
    public static final float Start_Y_QuitButton = 0.88f;
    public static final float width_QuitButton   = 200f;
    public static final float height_QuitButton  = 80f;

    public static boolean pendingButtonUpdate = false;
    public static boolean betDoubled = false;
    public static boolean playerBurst = false;
    public static boolean blackJack = false;
    public static boolean firstRoundEnded = false;

    ArrayList<Card> deck;
    Random random = new Random();

    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;
    
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    private BlackJackLogic() {
        // startGame(scene);
    }

    // GAME LOGIC
    public void startGame(Scene scene) {
        if (state != GameState.ROUND_START) return;
        if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
                scene.setGuiInstance(new BlackJackGui());
            }
        ((BlackJackGui) scene.getGuiInstance()).clearMessages();

        clearCards(scene);
        betDoubled = false;
        PlayerBet = 0;
        hiddenCardReavaled = false;
        firstRoundEnded = false;
        // Getting deck ready
        buildDeck();
        shuffleDeck();

        // Set game state
        // state = GameState.ROUND_START;
        System.out.println("Player's Capital: " + PlayerCapital);
        System.out.println("Current Bet " + PlayerBet);
        
        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
        BlackJackGui.GuiMessage capitalMsg = new BlackJackGui.GuiMessage(
            "CAPITAL_MESSAGE",
            "Current Capital: " + PlayerCapital,
            Start_X_PlayerCapital,
            Start_Y_PlayerCapital,
            PlayerCapital_Scale,
            1f, 1f, 1f, 1f
        );
        capitalMsg.anchor = "left";
        gui.addMessage(capitalMsg);


        BlackJackGui.GuiMessage betMsg = new BlackJackGui.GuiMessage(
            "BET_MESSAGE",
            "Current Bet: "+ PlayerBet,
            Start_X_PlayerBet,
            Start_Y_PlayerBet,
            PlayerBet_Scale,
            1f, 1f, 1f, 1f
        );
        betMsg.anchor = "left";
        gui.addMessage(betMsg);
        pendingButtonUpdate = true;
    }

    public void drawCards(Scene scene) {
        if (PlayerBet == 0) return;
        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
        String cardPath;

        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerSumNoHiddenCard = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;
        cardPath = hiddenCard.getPath();
        EntityLoader.loadCard(cardPath, scene, EntityLoader.CardType.HIDDEN);

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        cardPath = card.getPath();
        EntityLoader.loadCard(cardPath, scene, EntityLoader.CardType.DEALER);

        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
            cardPath = card.getPath();
            EntityLoader.loadCard(cardPath, scene, EntityLoader.CardType.PLAYER);

                    // Play card sound for each card dealt
            if (soundManager != null) {
                soundManager.playSFX("SFX_CARD");
                // Add slight delay for multiple cards or play once
        }
        }

        

        
        gui.removeMessageById("PlayerPoints");
        gui.removeMessageById("DealerPoints");

        BlackJackGui.GuiMessage playerPointsMsg = new BlackJackGui.GuiMessage(
            "PlayerPoints",
            "Player: " + playerSum,
            Start_X_PlayerPoints, 
            Start_Y_PlayerPoints,
            PlayerPoints_Scale,
            1f, 1f, 1f, 1f
        );
        playerPointsMsg.anchor = "left";
        gui.addMessage(playerPointsMsg);

        dealerSumNoHiddenCard = dealerSum - hiddenCard.getValue();

        if(hiddenCardReavaled) {
            BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                "DealerPoints",
                "Dealer: " + dealerSum,
                Start_X_DealerPoints, 
                Start_Y_DealerPoints,
                DealerPoints_Scale,
                1f, 1f, 1f, 1f
            );
            dealerPointsMsg.anchor = "left";
            gui.addMessage(dealerPointsMsg);
        }
        else {
            BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                "DealerPoints",
                "Dealer: " + dealerSumNoHiddenCard,
                Start_X_DealerPoints, 
                Start_Y_DealerPoints,
                DealerPoints_Scale,
                1f, 1f, 1f, 1f
            );
            dealerPointsMsg.anchor = "left";
            gui.addMessage(dealerPointsMsg);
        }
        pendingButtonUpdate = true;
        if (playerSum == 21) {
            blackJack = true;
        }
    }

    public void manageButtons(Scene scene) {
        if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
                scene.setGuiInstance(new BlackJackGui());
            }
        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
        gui.clearButtons();
        pendingButtonUpdate = false;;
        switch(state) {
            case NONE:
                break;
        
            case ROUND_START: 
                
                gui.addButton(new BlackJackGui.GuiButton(
                    "showCards_btn",
                    "BET",
                    Start_X_ShowCardsButton, 
                    Start_Y_ShowCardsButton,     
                    width_ShowCardsButton, 
                    height_ShowCardsButton,          
                    0.75f, 0.10f, 0.10f, 1f,
                    () -> {

                        if (soundManager != null) {
                        soundManager.playSFX("SFX_BUTTON_2");
                    }

                        if (PlayerBet == 0) return; 
                        changeGameStatetoPlayerTurn();
                        drawCards(scene);
                    }
                    
                    
                ));
                break;
            
            case PLAYER_TURN:
                if(firstRoundEnded) {
                    gui.removeButtonById("double_btn");
                    gui.addButton(new BlackJackGui.GuiButton(
                        "hit_btn",
                        "HIT",
                        Start_X_HitButton, 
                        Start_Y_HitButton,     
                        width_HitButton, 
                        height_HitButton,          
                        0.75f, 0.10f, 0.10f, 1f,
                        () -> {
                            hit(scene);
                        }
                    ));

                    gui.addButton(new BlackJackGui.GuiButton(
                        "stand_btn",
                        "STAND",
                        Start_X_StandButton, 
                        Start_Y_StandButton,     
                        width_StandButton, 
                        height_StandButton,          
                        0.75f, 0.10f, 0.10f, 1f,
                        () -> {

                            if (soundManager != null) {
                            soundManager.playSFX("SFX_BUTTON_2");
                            }

                            changeGameStatetoDealerTurn();
                            stand(scene);
                        }
                    ));
                }

                else
                {
                    gui.addButton(new BlackJackGui.GuiButton(
                        "hit_btn",
                        "HIT",
                        Start_X_HitButton, 
                        Start_Y_HitButton,     
                        width_HitButton, 
                        height_HitButton,          
                        0.75f, 0.10f, 0.10f, 1f,
                        () -> {


                            if (soundManager != null) {
                            soundManager.playSFX("SFX_BUTTON_2");
                            }

                            firstRoundEnded = true;
                            hit(scene);
                            pendingButtonUpdate = true;
                        }
                    ));

                    gui.addButton(new BlackJackGui.GuiButton(
                        "stand_btn",
                        "STAND",
                        Start_X_StandButton, 
                        Start_Y_StandButton,     
                        width_StandButton, 
                        height_StandButton,    
                        0.75f, 0.10f, 0.10f, 1f,
                        () -> {

                            if (soundManager != null) {
                            soundManager.playSFX("SFX_BUTTON_2");
                            }

                            firstRoundEnded = true;
                            changeGameStatetoDealerTurn();
                            stand(scene);
                            pendingButtonUpdate = true;
                        }
                    ));

                    gui.addButton(new BlackJackGui.GuiButton(
                        "double_btn",
                        "DOUBLE",
                        Start_X_DoubleButton, 
                        Start_Y_DoubleButton,     
                        width_DoubleButton, 
                        height_DoubleButton,          
                        0.75f, 0.10f, 0.10f, 1f,
                        () -> {

                            if (soundManager != null) {
                            soundManager.playSFX("SFX_BUTTON_2");
                            }

                            firstRoundEnded = true;
                            doubleBet(scene);

                            // Player receives exactly one final card
                            hit(scene);

                            // Player turn ends immediately when doubling
                            changeGameStatetoDealerTurn();

                            // Reveal dealer's hidden card
                            revealHiddenCard(scene);
                            hiddenCardReavaled = true;

                            // Now properly run dealer logic
                            finishDealer(scene);
                        }
                    ));
                    
                }
                break;
            case DEALER_TURN:
                break;
            case ROUND_OVER: 
            gui.addButton(new BlackJackGui.GuiButton(
                    "continue_btn",
                    "Continue",
                    Start_X_ContinueButton, 
                    Start_Y_ContinueButton,     
                    width_ContinueButton, 
                    height_ContinueButton,          
                    0.75f, 0.10f, 0.10f, 1f,
                    () -> {

                    if (soundManager != null) {
                            soundManager.playSFX("SFX_BUTTON_2");
                    }
                    
                    changeGameStatetoRoundStart();
                    startGame(scene);
                    scene.getCamera().setTopDownView(); 
                        
                    }
                ));

                gui.addButton(new BlackJackGui.GuiButton(
                    "quit_btn",
                    "Quit",
                    Start_X_QuitButton, 
                    Start_Y_QuitButton,     
                    width_QuitButton, 
                    height_QuitButton,          
                    0.75f, 0.10f, 0.10f, 1f,
                    () -> {

                    if (soundManager != null) {
                            soundManager.playSFX("SFX_BUTTON_2");
                    }
                    
                    scene.getCamera().setNormalView();
                    scene.clearCardEntities(EntityLoader.getCardModels());
                    EntityLoader.removeHiddenCard(scene);
                    EntityLoader.resetOffsets();
                    ((BlackJackGui) scene.getGuiInstance()).clearMessages();
                    EntityLoader.clearChips(scene); 
                    resetPlayerCapital();
                    }
                ));
                break;
            case GAME_OVER: 
                    gui.addButton(new BlackJackGui.GuiButton(
                    "NewGame_btn",
                    "New Game",
                    Start_X_NewGameButton, 
                    Start_Y_NewGameButton,     
                    width_NewGameButton, 
                    height_NewGameButton,          
                    0.75f, 0.10f, 0.10f, 1f,
                    () -> {
                    changeGameStatetoRoundStart();
                    resetPlayerCapital();
                    startGame(scene);
                    scene.getCamera().setTopDownView();    
                    }
                ));

                gui.addButton(new BlackJackGui.GuiButton(
                    "quit_btn",
                    "Quit",
                    Start_X_QuitButton, 
                    Start_Y_QuitButton,     
                    width_QuitButton, 
                    height_QuitButton,          
                    0.75f, 0.10f, 0.10f, 1f,
                    () -> {
                    scene.getCamera().setNormalView();
                    scene.clearCardEntities(EntityLoader.getCardModels());
                    EntityLoader.removeHiddenCard(scene);
                    EntityLoader.resetOffsets();
                    ((BlackJackGui) scene.getGuiInstance()).clearMessages();
                    EntityLoader.clearChips(scene); 
                    resetPlayerCapital();
                    }
                ));
                break;
        }
    }

    public void doubleBet(Scene scene) {
        if(betDoubled) return;
        PlayerBet = PlayerBet *2;

        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();

        
        gui.removeMessageById("BET_MESSAGE");

        BlackJackGui.GuiMessage betMsg = new BlackJackGui.GuiMessage(
            "BET_MESSAGE",
            "Current Bet: "+ PlayerBet,
            Start_X_PlayerBet,
            Start_Y_PlayerBet,
            PlayerBet_Scale,
            1f, 1f, 1f, 1f
        );
        betMsg.anchor = "left";
        gui.addMessage(betMsg);

        betDoubled = true;
    }
    public void changeGameStatetoPlayerTurn() {
        state = GameState.PLAYER_TURN; 
    }

    public void changeGameStatetoDealerTurn() {
        state = GameState.DEALER_TURN;
    }

    public void changeGameStatetoRoundStart() {
        state = GameState.ROUND_START;
    }

    public boolean checkBet() {
        if (PlayerBet == 0) {
            System.out.println("Must set a bet!");
            return false;
        }

        else {
            return true;
        }
    }

    public void hit(Scene scene) {
        if (state != GameState.PLAYER_TURN) return;

        Card card = deck.remove(deck.size() - 1);
        playerHand.add(card);

        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        reducePlayerAce();

        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
        gui.removeMessageById("PlayerPoints");

        BlackJackGui.GuiMessage playerPointsMsg = new BlackJackGui.GuiMessage(
            "PlayerPoints",
            "Player: " + playerSum,
            Start_X_PlayerPoints, 
            Start_Y_PlayerPoints,
            PlayerPoints_Scale,
            1f, 1f, 1f, 1f
        );
        playerPointsMsg.anchor = "left";
        gui.addMessage(playerPointsMsg);

        EntityLoader.loadCard(card.getPath(), scene, EntityLoader.CardType.PLAYER);

        if (playerSum > 21) {
            state = GameState.ROUND_OVER;
            playerBurst = true;
            revealHiddenCard(scene);
            finishDealer(scene);
        }
    }

    public void stand(Scene scene) {
        if (state != GameState.DEALER_TURN) return;

        hiddenCardReavaled = revealHiddenCard(scene);

        boolean needsMore = dealerDrawOneCard(scene);

        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
        gui.removeMessageById("DealerPoints");

        dealerSumNoHiddenCard = dealerSum - hiddenCard.getValue();

        if(hiddenCardReavaled) {
            BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                "DealerPoints",
                "Dealer: " + dealerSum,
                Start_X_DealerPoints, 
                Start_Y_DealerPoints,
                DealerPoints_Scale,
                1f, 1f, 1f, 1f
            );
            dealerPointsMsg.anchor = "left";
            gui.addMessage(dealerPointsMsg);
        }
        else {
            BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                "DealerPoints",
                "Dealer: " + dealerSumNoHiddenCard,
                Start_X_DealerPoints, 
                Start_Y_DealerPoints,
                DealerPoints_Scale,
                1f, 1f, 1f, 1f
            );
            dealerPointsMsg.anchor = "left";
            gui.addMessage(dealerPointsMsg);
        }

        if (!needsMore) {
            state = GameState.ROUND_OVER;
            decideWinner(scene);
        }

        // while (dealerSum < 17) {
        //     dealerDrawOneCard(scene);
        // }

        // // after finishing:
        // state = GameState.ROUND_OVER;
        // decideWinner(scene);

    }

    public boolean dealerDrawOneCard(Scene scene) {
        if (dealerSum >= 17) {
            return false; // No more cards to draw
        }

        Card card = deck.remove(deck.size() - 1);
        dealerHand.add(card);

        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        reduceDealerAce();

        EntityLoader.loadCard(card.getPath(), scene, EntityLoader.CardType.DEALER);

        if(!betDoubled) {
            state = GameState.PLAYER_TURN;
        }
        return dealerSum < 17; 
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"1","2","3","4","5","6","7","8","9","10","11","12","13"};
        String[] types  = {"clubs","diamonds","hearts","spades"};

        for (String t : types) {
            for (String v : values) {
                deck.add(new Card(v, t));
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }

    public static void betChips(String chipValue, Scene scene, float z) {
        if (z != 1.55f) return;
        if (state != GameState.ROUND_START) return;

        int correctedValue = Integer.parseInt(chipValue) / 10;
        int checkBetLimit = PlayerBet + correctedValue;

        if (checkBetLimit > PlayerCapital) {
            System.out.println("Not enough money ");
            return;
        }

        PlayerBet += correctedValue;
        System.out.println("Current Bet " + PlayerBet);

        // Ensure GUI exists
        if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
            scene.setGuiInstance(new BlackJackGui());
        }

        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();

        
        gui.removeMessageById("BET_MESSAGE");

        BlackJackGui.GuiMessage betMsg = new BlackJackGui.GuiMessage(
            "BET_MESSAGE",
            "Current Bet: "+ PlayerBet,
            Start_X_PlayerBet,
            Start_Y_PlayerBet,
            PlayerBet_Scale,
            1f, 1f, 1f, 1f
        );
        betMsg.anchor = "left";
        gui.addMessage(betMsg);

        if (soundManager != null) {
        soundManager.playRandomChipSound();
        }

        System.out.println("chipValue: " + chipValue);
        EntityLoader.moveBetChips(scene, chipValue);
    }

    public static void undoBetChips(String chipValue, Scene scene, float z) {

        if (z != 1.45f) return;
        if (state != GameState.ROUND_START) return;

        int correctedValue = Integer.parseInt(chipValue) / 10;
        int checkNotLessthan0 = PlayerBet - correctedValue;

        // ---- Ensure GUI exists ----
        if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
            scene.setGuiInstance(new BlackJackGui());
        }
        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();

        if (soundManager != null) {
        soundManager.playRandomChipSound();
        }

        // Remove any previous bet message (prevents overlap)
        gui.removeMessageById("BET_MESSAGE");

        // ---- CASE 1: Bet would go negative ----
        if (checkNotLessthan0 < 0) {
            PlayerBet = 0;
            System.out.println("Current Bet " + PlayerBet);

        BlackJackGui.GuiMessage betMsg = new BlackJackGui.GuiMessage(
            "BET_MESSAGE",
            "Current Bet: "+ PlayerBet,
            Start_X_PlayerBet,
            Start_Y_PlayerBet,
            PlayerBet_Scale,
            1f, 1f, 1f, 1f
        );
        betMsg.anchor = "left";
        gui.addMessage(betMsg);

            return;
        }

        // ---- CASE 2: Bet becomes ZERO ----
        if (checkNotLessthan0 == 0) {
            PlayerBet = 0;
            System.out.println("Current Bet " + PlayerBet);

        BlackJackGui.GuiMessage betMsg = new BlackJackGui.GuiMessage(
            "BET_MESSAGE",
            "Current Bet: "+ PlayerBet,
            Start_X_PlayerBet,
            Start_Y_PlayerBet,
            PlayerBet_Scale,
            1f, 1f, 1f, 1f
        );
        betMsg.anchor = "left";
        gui.addMessage(betMsg);

            EntityLoader.removeBetChips(scene, chipValue);
            return;
        }

        // ---- CASE 3: Normal reduction ----
        PlayerBet -= correctedValue;
        System.out.println("Current Bet " + PlayerBet);

        String betTxt = "Current Bet: " + PlayerBet;

        BlackJackGui.GuiMessage betMsg = new BlackJackGui.GuiMessage(
            "BET_MESSAGE",
            "Current Bet: "+ PlayerBet,
            Start_X_PlayerBet,
            Start_Y_PlayerBet,
            PlayerBet_Scale,
            1f, 1f, 1f, 1f
        );
        betMsg.anchor = "left";
        gui.addMessage(betMsg);

        System.out.println("chipValue: " + chipValue);
        EntityLoader.removeBetChips(scene, chipValue);
    }


    public boolean revealHiddenCard(Scene scene) {
        EntityLoader.replaceHiddedCard(hiddenCard.getPath(), scene);
        EntityLoader.removeHiddenCard(scene);
        return true;
    }

    public void finishDealer(Scene scene) {
        reduceDealerAce();
        System.out.println(hiddenCardReavaled);
        if(playerBurst)
        {
            BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
            gui.removeMessageById("DealerPoints");

            BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                "DealerPoints",
                "Dealer: " + dealerSum,
                Start_X_DealerPoints, 
                Start_Y_DealerPoints,
                DealerPoints_Scale,
                1f, 1f, 1f, 1f
            );
            dealerPointsMsg.anchor = "left";
            gui.addMessage(dealerPointsMsg);
        }
        else {
            while (dealerSum < 17) {
                Card card = deck.remove(deck.size() - 1);
                dealerHand.add(card);

                dealerSum += card.getValue();
                dealerAceCount += card.isAce() ? 1 : 0;
                reduceDealerAce();

                BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
                gui.removeMessageById("DealerPoints");

                dealerSumNoHiddenCard = dealerSum - hiddenCard.getValue();

                if(hiddenCardReavaled) {
                    BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                        "DealerPoints",
                        "Dealer: " + dealerSum,
                        Start_X_DealerPoints, 
                        Start_Y_DealerPoints,
                        DealerPoints_Scale,
                        1f, 1f, 1f, 1f
                    );
                    dealerPointsMsg.anchor = "left";
                    gui.addMessage(dealerPointsMsg);
                }
                else {
                    BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                        "DealerPoints",
                        "Dealer: " + dealerSumNoHiddenCard,
                        Start_X_DealerPoints, 
                        Start_Y_DealerPoints,
                        DealerPoints_Scale,
                        1f, 1f, 1f, 1f
                    );
                    dealerPointsMsg.anchor = "left";
                    gui.addMessage(dealerPointsMsg);        
                }

                EntityLoader.loadCard(card.getPath(), scene, EntityLoader.CardType.DEALER);
            } 
        }
        System.out.println(dealerSum);
        state = GameState.ROUND_OVER;
        decideWinner(scene);
        
    }

    public void decideWinner(Scene scene) {
        if (state != GameState.ROUND_OVER) return;
        pendingButtonUpdate = true;
        System.out.println("PLAYER POINTS: " + playerSum);
        System.out.println("DEALER POINTS: " + dealerSum);
        BlackJackGui gui = (BlackJackGui) scene.getGuiInstance();
        gui.removeMessageById("DealerPoints");
            BlackJackGui.GuiMessage dealerPointsMsg = new BlackJackGui.GuiMessage(
                "DealerPoints",
                "Dealer: " + dealerSum,
                Start_X_DealerPoints, 
                Start_Y_DealerPoints,
                DealerPoints_Scale,
                1f, 1f, 1f, 1f
            );

            dealerPointsMsg.anchor = "left";
            gui.addMessage(dealerPointsMsg);
       // --- 1. Player busts ---
        if (playerSum > 21) {
            System.out.println("GAME RESULT: PLAYER LOST!");
            if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
                scene.setGuiInstance(new BlackJackGui());
            }

            
            gui.addMessage(new BlackJackGui.GuiMessage(
                "Winner",
                "DEALER WINS!",
                Start_X_DecideWinner,   
                Start_Y_DecideWinner,   
                DecideWinner_Scale,
                1f, 1f, 1f, 1f  // light red
            ));
            
            PlayerCapital -= PlayerBet;   
            PlayerBet = 0;   

            if(PlayerCapital == 0) {
                System.out.println("GAME OVER");
                gui.removeMessageById("Winner");
                gui.addMessage(new BlackJackGui.GuiMessage(
                    "Game Over",
                    "GAME OVER",
                    Start_X_GameOver,   
                    Start_Y_GameOver,   
                    GameOver_Scale,
                    1f, 0f, 0f, 1f  // light red
                ));
                if(soundManager!=null){
                soundManager.playSFX("SFX_LOSE");
                }
                state = GameState.GAME_OVER;
                pendingButtonUpdate = true;
                
            }
            else {
                state = GameState.ROUND_OVER;
            }
            return;
        }

        // --- 2. Dealer busts ---
        if (dealerSum > 21) {
            System.out.println("GAME RESULT: PLAYER WINS!");
            if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
                scene.setGuiInstance(new BlackJackGui());
            }
            
            gui.addMessage(new BlackJackGui.GuiMessage(
                "Winner",
                "PLAYER WINS!",
                Start_X_DecideWinner,   
                Start_Y_DecideWinner,   
                DecideWinner_Scale,
                1f, 1f, 1f, 1f  // light red
            ));
            if(soundManager!=null){
                soundManager.playSFX("SFX_WIN");
            }

            if(blackJack) {
                PlayerCapital = PlayerCapital + PlayerBet * 2.5f; 
            }
            else {
                PlayerCapital = PlayerCapital + PlayerBet * 2;      
            }     
            PlayerBet = 0;
            state = GameState.ROUND_OVER;
            return;
        }

        // --- 3. Both not bust compare values ---

        // Exact tie
        if (playerSum == dealerSum) {
            System.out.println("GAME RESULT: DRAW");
            if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
                scene.setGuiInstance(new BlackJackGui());
            }
            
            gui.addMessage(new BlackJackGui.GuiMessage(
                "Winner",
                "DRAW!",
                Start_X_DecideWinner,   
                Start_Y_DecideWinner,   
                DecideWinner_Scale,
                1f, 1f, 1f, 1f  // light red
            ));
            PlayerBet = 0;
            state = GameState.ROUND_OVER;
            return;
        }

        // Player wins with higher total
        if (playerSum > dealerSum) {
            System.out.println("GAME RESULT: PLAYER WINS");
            if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
                scene.setGuiInstance(new BlackJackGui());
            }
            
            gui.addMessage(new BlackJackGui.GuiMessage(
                "Winner",
                "PLAYER WINS!",
                Start_X_DecideWinner,   
                Start_Y_DecideWinner,   
                DecideWinner_Scale,
                1f, 1f, 1f, 1f  // light red
            ));

            if(soundManager!=null){
                soundManager.playSFX("SFX_WIN");
            }
            
            if(blackJack) {
                PlayerCapital = PlayerCapital + PlayerBet * 2.5f; 
            }
            else {
                PlayerCapital = PlayerCapital + PlayerBet * 2;      
            }       
            PlayerBet = 0;    
            state = GameState.ROUND_OVER;
            return;
        }

        // Dealer wins with higher total
        if (dealerSum > playerSum) {
            System.out.println("GAME RESULT: DEALER WINS");
            if (!(scene.getGuiInstance() instanceof BlackJackGui)) {
                scene.setGuiInstance(new BlackJackGui());
            }
            
            gui.addMessage(new BlackJackGui.GuiMessage(
                "Winner",
                "DEALER WINS!",
                Start_X_DecideWinner,   
                Start_Y_DecideWinner,   
                DecideWinner_Scale,
                1f, 1f, 1f, 1f  
            ));
            PlayerCapital -= PlayerBet;
            PlayerBet = 0;     
            if(PlayerCapital == 0) {
                System.out.println("GAME OVER");
                
                gui.removeMessageById("Winner");
                gui.addMessage(new BlackJackGui.GuiMessage(
                    "Game Over",
                    "GAME OVER",
                    Start_X_GameOver,   
                    Start_Y_GameOver,   
                    GameOver_Scale,
                    1f, 0f, 0f, 1f 
                 ));

                 if(soundManager!=null){
                soundManager.playSFX("SFX_LOSE");
            }
                
                state = GameState.GAME_OVER;
                pendingButtonUpdate = true;
            }
            else {
                state = GameState.ROUND_OVER;
            }
            return;
        }
    }

    // public void gameOver(Scene scene) {
    //     scene.getCamera().setNormalView();
    //     scene.clearCardEntities(EntityLoader.getCardModels());
    //     EntityLoader.resetOffsets();
    //     EntityLoader.removeHiddenCard(scene);
    //     ((BlackJackGui) scene.getGuiInstance()).clearMessages();
    //     EntityLoader.clearChips(scene); 
    //     resetPlayerCapital();
    // }

    public void clearCards(Scene scene) {
        scene.clearCardEntities(EntityLoader.getCardModels());
        EntityLoader.clearChips(scene); 
                for (int i = 0; i < EntityLoader.CHIP_VALUES.length; i++) {
                    EntityLoader.loadChips(i, EntityLoader.CHIP_VALUES[i], scene);
                }
        EntityLoader.removeHiddenCard(scene);
        EntityLoader.resetOffsets();
        EntityLoader.resetCounter();
    }
    
    public void resetPlayerCapital() {
        PlayerCapital = 1000;
    }

    public static void keyCallBack(Window window, Scene scene) {

        glfwSetKeyCallback(window.getWindowHandle(), (handle, key, scancode, action, mods) -> {

            window.keyCallBack(key, action);

            BlackJackLogic logic = BlackJackLogic.getInstance();

            // Start game
            if (key == GLFW_KEY_P && action == GLFW_RELEASE) {
                logic.changeGameStatetoRoundStart();
                logic.startGame(scene);
                scene.getCamera().setTopDownView(); 
            }

            // Hit
            if (key == GLFW_KEY_H && action == GLFW_RELEASE) {
                if (logic.checkBet())
                {
                    logic.hit(scene);
                }   
                
            }

            // Stand
            if (key == GLFW_KEY_J && action == GLFW_RELEASE) {
                if (logic.checkBet())
                {
                    logic.changeGameStatetoDealerTurn();
                    logic.stand(scene);
                }   
                
            }

            // End Game
            if (key == GLFW_KEY_Q && action == GLFW_RELEASE) {
                scene.getCamera().setNormalView();
                scene.clearCardEntities(EntityLoader.getCardModels());
                EntityLoader.removeHiddenCard(scene);
                EntityLoader.resetOffsets();
                ((BlackJackGui) scene.getGuiInstance()).clearMessages();
                EntityLoader.clearChips(scene); 
                logic.resetPlayerCapital();
            }

            if (key == GLFW_KEY_M && action == GLFW_RELEASE) {
                logic.drawCards(scene);
            }
        });
    }
}
