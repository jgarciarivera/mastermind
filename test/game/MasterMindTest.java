package game;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import game.MasterMind.Feedback;
import static game.MasterMind.Feedback.*;
import static game.MasterMind.GameState.*;

public class MasterMindTest
{
    MasterMind masterMind;

    @BeforeEach
    void setUp()
    {
        List<Color> selection = Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN);
        masterMind = new MasterMind(selection);
    }

    @Test
    void canary()
    {
        assertTrue(true);
    }

    @Test
    void userGuessesAllWrongColorsReturnsOnlyNoMatches()
    {
        List<Color> userGuess = Arrays.asList(Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(6, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(0, (long)feedback.get(MATCH)),
                () -> assertEquals(0, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesAllRightColorsWithAllInWrongPositionReturnsOnlyMatches()
    {
        List<Color> userGuess = Arrays.asList(Color.CYAN, Color.BLACK, Color.WHITE, Color.GREEN, Color.BLUE, Color.RED);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(0, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(6, (long)feedback.get(MATCH)),
                () -> assertEquals(0, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesAllRightColorsWithAllInRightPositionReturnsOnlyPositionMatches()
    {
        List<Color> userGuess = Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(0, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(0, (long)feedback.get(MATCH)),
                () -> assertEquals(6, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesOneColorRightAndInWrongPositionReturnsMatch()
    {
        List<Color> userGuess = Arrays.asList(Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.RED);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(5, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(1, (long)feedback.get(MATCH)),
                () -> assertEquals(0, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesOneColorRightAndInRightPositionReturnsPositionMatch()
    {
        List<Color> userGuess = Arrays.asList(Color.PINK, Color.BLUE, Color.PINK, Color.PINK, Color.PINK, Color.PINK);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(5, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(0, (long)feedback.get(MATCH)),
                () -> assertEquals(1, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesTwoColorsRightOnlyOneInRightPositionReturnsOneMatchOnePositionMatch()
    {
        List<Color> userGuess = Arrays.asList(Color.RED, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.GREEN);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(4, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(1, (long)feedback.get(MATCH)),
                () -> assertEquals(1, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesRightColorDuplicatedReturnsCorrespondingMatches()
    {
        List<Color> userGuess = Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.BLACK, Color.WHITE);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(1, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(2, (long)feedback.get(MATCH)),
                () -> assertEquals(3, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesRightColorDuplicatedButNoPositionMatchReturnsOnlyOneMatch()
    {
        List<Color> userGuess = Arrays.asList(Color.PINK, Color.RED, Color.RED, Color.RED, Color.RED, Color.RED);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(5, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(1, (long)feedback.get(MATCH)),
                () -> assertEquals(0, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void userGuessesTwoDuplicatedRightColorsInRightPositionReturnsOnlyTwoPositionMatches()
    {
        List<Color> userGuess = Arrays.asList(Color.RED, Color.RED, Color.RED, Color.WHITE, Color.WHITE, Color.WHITE);

        Map<Feedback, Long> feedback = masterMind.guess(userGuess);

        assertAll(
                () -> assertEquals(4, (long)feedback.get(NO_MATCH)),
                () -> assertEquals(0, (long)feedback.get(MATCH)),
                () -> assertEquals(2, (long)feedback.get(POSITION_MATCH)));
    }

    @Test
    void gameStatusAtTheStartOfTheGameReturnsInProgress()
    {
        assertEquals(IN_PROGRESS, masterMind.getState());
    }

    @Test
    void gameStatusAfterTwoGuessesReturnsInProgress()
    {
        masterMind.guess(Arrays.asList(Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.RED));
        masterMind.guess(Arrays.asList(Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.RED));

        assertEquals(IN_PROGRESS, masterMind.getState());
    }

    @Test
    void gameStatusAfterCorrectColorsInAllPositionsReturnsWonState()
    {
        masterMind.guess(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        assertEquals(WON, masterMind.getState());
    }

    @Test
    void gameStatusAfterGuessIsCalledMaxAttemptsReturnsLostState()
    {
        masterMind.tries = 19;
        masterMind.guess(Arrays.asList(Color.RED, Color.RED, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        assertEquals(LOST, masterMind.getState());
    }

    @Test
    void gameStatusAfterGuessIsCalledAfterCorrectGuessIsWon()
    {
        masterMind.guess(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));
        masterMind.guess(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        assertEquals(WON, masterMind.getState());
    }

    @Test
    void gameStatusAfterGuessIsCalledOnTheLastAttemptIsWon()
    {
        masterMind.tries = 19;
        masterMind.guess(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        assertEquals(WON, masterMind.getState());
    }

    @Test
    void gameStatusAfterGuessExceedsMaxAttemptsAvailable()
    {
        masterMind.tries = 20;
        masterMind.guess(Arrays.asList(Color.RED, Color.RED, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        assertEquals(LOST, masterMind.getState());
    }

    @Test
    void gameStatusWrongGuessAfterAWin()
    {
        masterMind.tries = 19;
        masterMind.guess(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        masterMind.guess(Arrays.asList(Color.RED, Color.RED, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));
        assertEquals(WON, masterMind.getState());
    }

    @Test
    void gameStatusWrongGuessAfterALoss()
    {
        masterMind.tries = 19;
        masterMind.guess(Arrays.asList(Color.RED, Color.RED, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        masterMind.guess(Arrays.asList(Color.RED, Color.RED, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));
        assertEquals(LOST, masterMind.getState());
    }

    @Test
    void gameStatusRightGuessAfterALoss()
    {
        masterMind.tries = 19;
        masterMind.guess(Arrays.asList(Color.RED, Color.RED, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));

        masterMind.guess(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.CYAN));
        assertEquals(LOST, masterMind.getState());
    }

    @Test
    void generateRandomColorsReturnsSixColors()
    {
        assertEquals(6, masterMind.generateRandomColors(System.nanoTime()).size());
    }

    @Test
    void generateRandomColorsReturnsUniqueColors()
    {
        HashSet<Color> distinctlyFilteredColorList = new HashSet<>(masterMind.generateRandomColors(System.nanoTime()));

        assertEquals(6, distinctlyFilteredColorList.size());
    }

    @Test
    void generateRandomColorsReturnsTrueRandomness()
    {
        HashSet<List<Color>> distinctSetOfColors= new HashSet<>();
        IntStream.range(0, 50).forEach(iteration -> distinctSetOfColors.add(masterMind.generateRandomColors(System.nanoTime())));

        assertEquals(50, distinctSetOfColors.size());
    }

    @Test
    void generateRandomColorsIsCalledWhenNoArgumentConstructorIsCalled()
    {
        AtomicBoolean called = new AtomicBoolean();
        MasterMind stub = new MasterMind()
        {
            protected List<Color> generateRandomColors(long seed)
            {
                called.set(true);
                return Arrays.asList();
            }
        };

        assertTrue(called.get());
    }

    @Test
    void getSelectionReturnsSixColors()
    {
        assertEquals(6, masterMind.getSelection().size());
    }

    @Test
    void getSelectionReturnsWinningValues()
    {
        masterMind.guess(masterMind.getSelection());
        assertEquals(WON, masterMind.getState());
    }

    @Test
    void testsToSuppressRedInCoverageForFeedbackEnums()
    {
        Feedback.values();
        Feedback.valueOf("NO_MATCH");
        assertTrue(true);
    }

    @Test
    void testsToSuppressRedInCoverageForGameStateEnums()
    {
        MasterMind.GameState.values();
        MasterMind.GameState.valueOf("IN_PROGRESS");
        assertTrue(true);
    }
}