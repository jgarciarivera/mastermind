package game.ui;

import game.MasterMind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MasterMindUI extends JFrame
{
    JPanel masterPanel;
    JScrollPane scrollPane;
    JPanel panelInsideScroll;
    JPanel panelToHoldGameHistory;
    JPanel panelToHoldSolution;
    JButton buttons[][];
    JButton gameHistory[][];
    JButton solutionFeedbackButtons[];
    JButton submit;
    JLabel instruction;
    MasterMind masterMind;
    JButton giveUp;
    int currentRow;


    @Override
    public void frameInit()
    {
        masterMind = new MasterMind();
        currentRow = 0;
        masterPanel = new JPanel();
        panelInsideScroll = new JPanel();
        panelToHoldGameHistory = new JPanel();
        panelInsideScroll.setLayout(new GridLayout(20, 6));
        buttons = new JButton[20][6];
        gameHistory = new JButton[20][6];
        solutionFeedbackButtons = new JButton[6];
        submit = new JButton("Submit");
        giveUp = new JButton("I Give Up!");
        instruction = new JLabel("   Your Guess (click to select color)");
        addSubmitButton();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.frameInit();
        createMenuBar();
        addSolutionFeedbackPanel();
        addGameHistory();
        addClickFunctionality();

        setTitle("Master Mind");
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        super.pack();
        super.setVisible(true);
    }

    private void addGameHistory()
    {
        panelToHoldGameHistory.setLayout(new GridBagLayout());
        JPanel gameHistoryPanel = new JPanel();
        gameHistoryPanel.setLayout(new GridLayout(20, 6));

        GridBagConstraints constraints = new GridBagConstraints();

        for (int rows = 0; rows < 20; rows++)
        {
            for (int columns = 0; columns < 6; columns++)
            {
                gameHistory[rows][columns] = new JButton(" ");
                gameHistoryPanel.add(gameHistory[rows][columns]);
            }
        }

        panelToHoldGameHistory.add(gameHistoryPanel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panelToHoldGameHistory.add(gameHistoryPanel, constraints);
        super.add(BorderLayout.EAST, panelToHoldGameHistory);

    }

    private void addSubmitButton()
    {
        submit.setMaximumSize(new Dimension(20, 20));
        submit.addActionListener(action ->
        {
            Map<MasterMind.Feedback, Long> feedback = masterMind.guess(Arrays.asList(solutionFeedbackButtons[0].getBackground(),
                    solutionFeedbackButtons[1].getBackground(),
                    solutionFeedbackButtons[2].getBackground(),
                    solutionFeedbackButtons[3].getBackground(),
                    solutionFeedbackButtons[4].getBackground(),
                    solutionFeedbackButtons[5].getBackground()));

            List<MasterMind.Feedback> feedBackList = new ArrayList<>();
            feedback.forEach((value, index) -> IntStream.range(0, index.intValue()).forEach( position -> feedBackList.add(value)));

            for (int index = 0; index < 6; index++)
            {
                if (feedBackList.get(index) == MasterMind.Feedback.POSITION_MATCH)
                    gameHistory[currentRow][index].setBackground(Color.BLACK);
                if (feedBackList.get(index) == MasterMind.Feedback.MATCH)
                    gameHistory[currentRow][index].setBackground(Color.WHITE);
                if (feedBackList.get(index) == MasterMind.Feedback.NO_MATCH)
                    gameHistory[currentRow][index].setBackground(Color.GRAY);
            }

            for (int index = 0; index < 6; index++)
                buttons[currentRow][index].setBackground(solutionFeedbackButtons[index].getBackground());


            currentRow = currentRow + 1;
            if (masterMind.getState() == MasterMind.GameState.WON)
            {
                showSolution();
                panelToHoldSolution.remove(instruction);
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.gridx = 0;
                constraints.gridy = 0;
                panelToHoldSolution.add(new JLabel(" SOLUTION"), constraints);
                panelToHoldSolution.updateUI();
                JOptionPane.showMessageDialog(null, "You've Won! \nSolution shown on guess panel. ");
                frameInit();
            }
            if (masterMind.getState() == MasterMind.GameState.LOST)
            {
                showSolution();
                panelToHoldSolution.remove(instruction);
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.gridx = 0;
                constraints.gridy = 0;
                panelToHoldSolution.add(new JLabel(" SOLUTION"), constraints);
                panelToHoldSolution.updateUI();
                JOptionPane.showMessageDialog(null, "You've Lost! \nSolution shown on guess panel. ");
                frameInit();
            }
        });
        super.add(BorderLayout.SOUTH, giveUp);
    }

    private void addSolutionFeedbackPanel()
    {
        JPanel solutionFeedBackPanel = new JPanel();
        solutionFeedBackPanel.setLayout(new FlowLayout());

        for (int index = 0; index < 6; index++)
        {
            solutionFeedbackButtons[index] = new JButton("");
            solutionFeedBackPanel.add(solutionFeedbackButtons[index]);
        }

        panelToHoldSolution = new JPanel();
        panelToHoldSolution.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panelToHoldSolution.add(instruction, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        panelToHoldSolution.add(solutionFeedBackPanel, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        panelToHoldSolution.add(submit, constraints);

        giveUp.addActionListener( action -> {
            showSolution();
            panelToHoldSolution.remove(instruction);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = 0;
            panelToHoldSolution.add(new JLabel(" SOLUTION"), constraints);
            panelToHoldSolution.updateUI();
            JOptionPane.showMessageDialog(null, "You've given up! \nSolution shown on guess panel. ");
            frameInit();
        });

        super.add(BorderLayout.WEST, panelToHoldSolution);
    }

    private void createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");        for (int rows = 0; rows < 20; rows++)
        for (int columns = 0; columns < 6; columns++)
        {
            buttons[rows][columns] = new JButton(" ");
            panelInsideScroll.add(buttons[rows][columns]);
        }

        scrollPane = new JScrollPane(panelInsideScroll);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setRowHeaderView(new JLabel("Attempts"));
        scrollPane.setPreferredSize(new Dimension(500, 500));

        masterPanel.add(panelInsideScroll);
        super.getContentPane().add(BorderLayout.CENTER, masterPanel);

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener((ActionEvent) ->frameInit());
        menu.add(newGame);
        menuBar.add(menu);
        super.add(BorderLayout.NORTH, menuBar);
    }

    public void addClickFunctionality()
    {
        JPopupMenu buttonSubMenu = new JPopupMenu("Menu");
        masterMind.getAvailableColors().forEach(color ->
        {
            JMenuItem menuItem = new JMenuItem("");
            menuItem.setBackground(color);
            buttonSubMenu.add(menuItem);
            menuItem.addActionListener(action -> buttonSubMenu.getInvoker().setBackground(color));
        });

        solutionFeedbackButtons[0].addActionListener(action -> buttonSubMenu.show(solutionFeedbackButtons[0],
                solutionFeedbackButtons[0].getBounds().x,solutionFeedbackButtons[0].getBounds().y));
        solutionFeedbackButtons[1].addActionListener(action -> buttonSubMenu.show(solutionFeedbackButtons[1],
                solutionFeedbackButtons[0].getX(),solutionFeedbackButtons[0].getY() ));
        solutionFeedbackButtons[2].addActionListener(action -> buttonSubMenu.show(solutionFeedbackButtons[2],
                solutionFeedbackButtons[0].getBounds().x,solutionFeedbackButtons[0].getBounds().y));
        solutionFeedbackButtons[3].addActionListener(action -> buttonSubMenu.show(solutionFeedbackButtons[3],
                solutionFeedbackButtons[0].getBounds().x,solutionFeedbackButtons[0].getBounds().y));
        solutionFeedbackButtons[4].addActionListener(action -> buttonSubMenu.show(solutionFeedbackButtons[4],
                solutionFeedbackButtons[0].getBounds().x,solutionFeedbackButtons[0].getBounds().y));
        solutionFeedbackButtons[5].addActionListener(action -> buttonSubMenu.show(solutionFeedbackButtons[5],
                solutionFeedbackButtons[0].getBounds().x,solutionFeedbackButtons[0].getBounds().y));
    }

    public void showSolution()
    {
        List<Color> solution = masterMind.getSelection();

        for (int index = 0; index < 6; index++)
            solutionFeedbackButtons[index].setBackground(solution.get(index));
    }

    public static void main(String[] args)
    {
        JFrame frame = new MasterMindUI();
        frame.pack();
        frame.setVisible(true);
    }
}
