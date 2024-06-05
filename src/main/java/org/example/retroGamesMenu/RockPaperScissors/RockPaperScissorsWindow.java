package org.example.retroGamesMenu.RockPaperScissors;

import org.example.RetroGamesClient;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RockPaperScissorsWindow extends JPanel {
    private final JLabel leftLabel;
    private final JLabel rightLabel;
    private Timer leftTimer; // Timer for the left animation
    private Timer rightTimer;

    public RockPaperScissorsWindow(RetroGamesMenu parentFrame) {


        setLayout(new BorderLayout());


        JLabel titleLabel = new JLabel("Rock Paper Scissors");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        int topSpace = 20;
        int leftSpace = 0;
        int bottomSpace = 0;
        int rightSpace = 0;
        setBorder(BorderFactory.createEmptyBorder(topSpace, leftSpace, bottomSpace, rightSpace));

        JPanel centralPanel = new JPanel(new BorderLayout());

        // Create the buttons with photos
        JButton button1 = createButtonWithPhoto("/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/rock.png");
        JButton button2 = createButtonWithPhoto("/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/paper.png");
        JButton button3 = createButtonWithPhoto("/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/scissors.png");

        // Set the preferred size of the buttons
        Dimension buttonSize = new Dimension(160, 160);
        button1.setPreferredSize(buttonSize);
        button2.setPreferredSize(buttonSize);
        button3.setPreferredSize(buttonSize);

        button1.addActionListener(e -> sendMoveToServer("rock", this::handleServerResponse));

        button2.addActionListener(e -> sendMoveToServer("paper", this::handleServerResponse));

        button3.addActionListener(e -> sendMoveToServer("scissors", this::handleServerResponse));

        // Add the buttons to the central panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(button1);
        buttonsPanel.add(button2);
        buttonsPanel.add(button3);
        centralPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add an animated photo to the central panel
        JPanel animationPanel = new JPanel(new BorderLayout());

        // Create the left animated label
        leftLabel = new JLabel();
        leftLabel.setPreferredSize(new Dimension(160, 160));
        animateLabel(leftLabel, "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/rock-left.png");

        // Create the right animated label
        rightLabel = new JLabel();
        rightLabel.setPreferredSize(new Dimension(160, 160));
        animateLabel(rightLabel, "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/rock-right.png");

        // Add the labels to the animation panel
        animationPanel.add(leftLabel, BorderLayout.WEST);
        animationPanel.add(rightLabel, BorderLayout.EAST);

        JPanel playerAiPanel = new JPanel(new BorderLayout());
        playerAiPanel.setBorder(BorderFactory.createEmptyBorder(100, 40, 0, 70)); // Add left and right margins


        JLabel playerLabel = new JLabel("Player");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Set a larger font size for Player label
        playerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerAiPanel.add(playerLabel, BorderLayout.WEST);

        JLabel aiLabel = new JLabel("AI");
        aiLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Set a larger font size for AI label
        aiLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        playerAiPanel.add(aiLabel, BorderLayout.EAST);

        centralPanel.add(playerAiPanel, BorderLayout.NORTH);


        // Add the animation panel to the central panel
        centralPanel.add(animationPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        JButton newBackButton = new JButton("Back");
        newBackButton.addActionListener(e -> {
            System.out.println("Back Rock Paper Scissors");
            RetroGamesClient.getInstance().sendMessageToServer("backRPS");
            parentFrame.recreateRetroGamesMenu(); // Go back to the game mode menu
        });
        southPanel.add(newBackButton, BorderLayout.CENTER);



        // Add the south panel to your main container
        add(southPanel, BorderLayout.SOUTH);

        add(titleLabel, BorderLayout.NORTH);

        add(centralPanel, BorderLayout.CENTER);
    }


    private JButton createButtonWithPhoto(String photoFilename) {
        // Load the photo
        ImageIcon imageIcon = new ImageIcon(photoFilename);

        // Resize the photo to 160x160 pixels
        Image image = imageIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);

        // Create a button with the resized photo
        JButton button = new JButton(imageIcon);
        button.setBorderPainted(false);  // Remove the border around the photo
        button.setContentAreaFilled(false);  // Make the button transparent

        return button;
    }

    private void animateLabel(JLabel label, String photoPath) {
        if (label == leftLabel && leftTimer != null) {
            leftTimer.stop(); // Stop the previous left animation
        } else if (label == rightLabel && rightTimer != null) {
            rightTimer.stop(); // Stop the previous right animation
        }

        ImageIcon imageIcon = new ImageIcon(photoPath);
        // Rotate the image if needed
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(140, 140, Image.SCALE_SMOOTH);

        // Create an ImageIcon with the resized and rotated image
        ImageIcon animatedImageIcon = new ImageIcon(resizedImage);

        // Set the animated ImageIcon to the label

        label.setIcon(animatedImageIcon);

        Timer timer = new Timer(50, new ActionListener() {
            private int yOffset = 0;
            private int direction = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                yOffset += direction;
                label.setLocation(label.getX(), label.getY() + direction);
                if (yOffset >= 5 || yOffset <= -5) {
                    direction *= -1;
                }
            }
        });

        if (label == leftLabel) {
            leftTimer = timer; // Store the timer for the left animation
        } else if (label == rightLabel) {
            rightTimer = timer; // Store the timer for the right animation
        }

        timer.setRepeats(true);
        timer.start();
    }



    private void handleServerResponse() {
        handleServerMessage(null);
    }


    private void sendMoveToServer(String move,Runnable callback) {

        RetroGamesClient.getInstance().sendMessageToServer(move);
        handleServerMessage(callback);
    }


    public void handleServerMessage(Runnable callback) {
        String serverMessage = RetroGamesClient.getInstance().getLastReceivedMessage();

        if (serverMessage != null && serverMessage.startsWith("Server move:")) {
            // Handle subsequent server messages
            String[] parts = serverMessage.split(", ");
            String serverMove = parts[0].substring(parts[0].lastIndexOf(":") + 1).trim();
            String playerMove = parts[1].substring(parts[1].lastIndexOf(":") + 1).trim();
            String gameState = parts[2].substring(parts[2].lastIndexOf(":") + 1).trim();

            updateAnimation(serverMove, playerMove,gameState);


            if (callback != null) {

                callback.run();

            }
        }
    }





    public void updateAnimation(String serverMove, String playerMove, String gameState) {
        String serverPhotoPath = getPhotoPathServer(serverMove);
        String playerPhotoPath = getPhotoPathPlayer(playerMove);

        // Stop the animation timers
        if (leftTimer != null && leftTimer.isRunning()) {
            leftTimer.stop();
        }
        if (rightTimer != null && rightTimer.isRunning()) {
            rightTimer.stop();
        }

        // Load the server photo
        ImageIcon serverImageIcon = new ImageIcon(serverPhotoPath);
        Image serverImage = serverImageIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        serverImageIcon = new ImageIcon(serverImage);

        // Load the player photo
        ImageIcon playerImageIcon = new ImageIcon(playerPhotoPath);
        Image playerImage = playerImageIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        playerImageIcon = new ImageIcon(playerImage);

        // Set the new image icons to the labels
        leftLabel.setIcon(playerImageIcon);
        rightLabel.setIcon(serverImageIcon);



        // Start the animation timers
        if (leftTimer != null) {
            leftTimer.start();
        }
        if (rightTimer != null) {
            rightTimer.start();
        }

    }


    private String getPhotoPathPlayer(String move) {
        return switch (move) {
            case "1" -> "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/rock-left.png";
            case "2" -> "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/paper-left.png";
            case "3" -> "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/scissors-left.png";
            default -> "";
        };
    }
    private String getPhotoPathServer(String move) {
        return switch (move) {
            case "1" -> "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/rock-right.png";
            case "2" -> "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/paper-right.png";
            case "3" -> "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/scissors-right.png";
            default -> "";
        };
    }
}