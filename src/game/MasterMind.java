package game;

import java.awt.*;
import java.util.*;
import java.util.List;

import java.util.stream.IntStream;
import java.util.function.IntFunction;
import java.util.function.Function;

import static java.awt.Color.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.counting;

import static game.MasterMind.Feedback.*;
import static game.MasterMind.GameState.*;

public class MasterMind
{
    public enum Feedback { NO_MATCH, MATCH, POSITION_MATCH }

    public enum GameState { IN_PROGRESS, LOST, WON }

    protected GameState gameState = IN_PROGRESS;

    protected int tries = 0;

    private final int SIZE = 6;
    private final int MAX_ATTEMPTS = 20;

    private List<Color> selection;

    public List<Color> getAvailableColors()
    {
        return new ArrayList<>(Arrays.asList(GREEN, BLUE, YELLOW, RED, PINK, ORANGE, MAGENTA, GRAY, DARK_GRAY, LIGHT_GRAY));
    }

    protected MasterMind(List<Color> selectedColors)
    {
        selection = selectedColors;
    }

    public MasterMind()
    {
        selection = generateRandomColors(System.nanoTime());
    }

    protected List<Color> generateRandomColors(long seed)
    {                                                                              
        Random random = new Random(seed);
        List<Color> gameColors = getAvailableColors();
        Collections.shuffle(gameColors, random);

        return gameColors.subList(0, SIZE);
    }

    public Map<Feedback, Long> guess(List<Color> userGuess)
    {
        tries = tries + 1;
      
        IntFunction<Feedback> computeMatchAtPosition = index ->
          selection.get(index) == userGuess.get(index) ? POSITION_MATCH :
            userGuess.contains(selection.get(index)) ? MATCH : NO_MATCH;

         Map<Feedback, Long> feedback =
           IntStream.range(0, SIZE)
             .mapToObj(computeMatchAtPosition)
             .collect((groupingBy(Function.identity(), counting())));

         feedback.computeIfAbsent(POSITION_MATCH, key -> 0L);
         feedback.computeIfAbsent(MATCH, key -> 0L);
         feedback.computeIfAbsent(NO_MATCH, key -> 0L);

        updateGameStatus(feedback);

        return feedback;
    }                                                                                
    
    public void updateGameStatus(Map<Feedback, Long> guess)
    {

       gameState = (tries <= MAX_ATTEMPTS && guess.get(POSITION_MATCH) == SIZE  || gameState == WON) ? WON : (tries >= MAX_ATTEMPTS) ? LOST: IN_PROGRESS;
    }

    public GameState getState()
    {
        return gameState;
    }

    public List<Color> getSelection() {
        return selection;
    }
}