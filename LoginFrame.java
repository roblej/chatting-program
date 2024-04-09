package final_project;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 175);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("사용자명:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 185, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 185, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(100, 80, 80, 25);
        panel.add(loginButton);

        JButton registerButton = new JButton("회원가입");
        registerButton.setBounds(185, 80, 100, 25);
        panel.add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(); // 로그인을 위한 메소드 호출
            }
        });

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(enter, "LOGIN");
        ActionMap actionMap = panel.getActionMap();
        actionMap.put("LOGIN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(); // 로그인을 위한 메소드 호출
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 새로운 회원가입 창을 열기
                JFrame registerFrame = new JFrame("회원가입");
                registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                registerFrame.setSize(320, 200);
                registerFrame.setLocationRelativeTo(null);

                JPanel registerPanel = new JPanel();
                registerFrame.add(registerPanel);
                registerPanel.setLayout(null);

                // 회원가입 폼 요소 추가
                JLabel newUserLabel = new JLabel("새 사용자명:");
                newUserLabel.setBounds(10, 20, 100, 25);
                registerPanel.add(newUserLabel);

                JTextField newUsernameField = new JTextField(20);
                newUsernameField.setBounds(120, 20, 165, 25);
                registerPanel.add(newUsernameField);

                JLabel newPasswordLabel = new JLabel("새 비밀번호:");
                newPasswordLabel.setBounds(10, 50, 100, 25);
                registerPanel.add(newPasswordLabel);

                JPasswordField newPasswordField = new JPasswordField(20);
                newPasswordField.setBounds(120, 50, 165, 25);
                registerPanel.add(newPasswordField);

                JButton registerSubmitButton = new JButton("가입하기");
                registerSubmitButton.setBounds(100, 90, 100, 25);
                registerPanel.add(registerSubmitButton);

                registerSubmitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String newUsername = newUsernameField.getText();
                        String newPassword = new String(newPasswordField.getPassword());
                        try {
							Class.forName("oracle.jdbc.driver.OracleDriver");
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        try (Connection connection = DatabaseConnection.getConnection()) {
                        	String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                        	PreparedStatement preparedStatement = connection.prepareStatement(query);

                        	// 중복된 username이 있는지 확인
                        	boolean usernameExists = false;
                        	String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
                        	PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                        	checkStatement.setString(1, newUsername);
                        	ResultSet resultSet = checkStatement.executeQuery();
                        	if (resultSet.next()) {
                        	    int count = resultSet.getInt(1);
                        	    if (count > 0) {
                        	        usernameExists = true;
                        	    }
                        	}

                        	if (!usernameExists && !newUsername.isEmpty() && !newPassword.isEmpty()) {
                        	    preparedStatement.setString(1, newUsername);
                        	    preparedStatement.setString(2, newPassword);

                        	    int result = preparedStatement.executeUpdate();
                        	    if (result > 0) {
                        	    	JFrame successFrame = new JFrame("등록 성공");
                                    JLabel successLabel = new JLabel("사용자가 등록되었습니다.");
                                    successLabel.setHorizontalAlignment(JLabel.CENTER);
                                    successFrame.getContentPane().add(successLabel);
                                    successFrame.setSize(300, 100);
                                    successFrame.setLocationRelativeTo(null);
                                    successFrame.setVisible(true);
                                    registerFrame.dispose(); // 등록 창 닫기
                        	    } else {
                        	    	JFrame failFrame = new JFrame("등록 실패");
                                    JLabel failLabel = new JLabel("사용자 등록에 실패했습니다.");
                                    failLabel.setHorizontalAlignment(JLabel.CENTER);
                                    failFrame.getContentPane().add(failLabel);
                                    failFrame.setSize(300, 100);
                                    failFrame.setLocationRelativeTo(null);
                                    failFrame.setVisible(true);
                        	    }
                        	} else {
                        	    if (usernameExists) {
                        	    	JFrame existFrame = new JFrame("등록 실패");
                                    JLabel existLabel = new JLabel("이미 존재하는 사용자명입니다.");
                                    existLabel.setHorizontalAlignment(JLabel.CENTER);
                                    existFrame.getContentPane().add(existLabel);
                                    existFrame.setSize(300, 100);
                                    existFrame.setLocationRelativeTo(null);
                                    existFrame.setVisible(true);
                        	    } else {
                        	    	JFrame emptyFrame = new JFrame("등록 실패");
                                    JLabel emptyLabel = new JLabel("사용자명 또는 비밀번호를 입력해주세요.");
                                    emptyLabel.setHorizontalAlignment(JLabel.CENTER);
                                    emptyFrame.getContentPane().add(emptyLabel);
                                    emptyFrame.setSize(300, 100);
                                    emptyFrame.setLocationRelativeTo(null);
                                    emptyFrame.setVisible(true);
                        	    }
                        	}
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                registerFrame.setVisible(true);
            }
        });
    }

 // 로그인 처리 메소드
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // 데이터베이스에서 일치하는 사용자가 있다면 로그인 성공
                String loggedInUsername = resultSet.getString("username");

                // 여기에 서버 실행 코드 추가
                startServer();

                // 채팅 애플리케이션 실행
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new MessengerApp(loggedInUsername);
                        dispose(); // 현재 창 닫기
                    }
                });
            } else {
                // 데이터베이스에서 일치하는 사용자가 없다면 로그인 실패
                JFrame failFrame = new JFrame("로그인 실패");
                JLabel failLabel = new JLabel("일치하지 않는 아이디 또는 비밀번호 입니다.");
                failLabel.setHorizontalAlignment(JLabel.CENTER);
                failFrame.getContentPane().add(failLabel);
                failFrame.setSize(300, 100);
                failFrame.setLocationRelativeTo(null);
                failFrame.setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    // 서버 실행 메소드
    private void startServer() {
        // 여기에 서버 실행 코드 추가
        new Thread(new ServerRunnable()).start();
    }

    // Server 클래스를 Runnable을 구현한 내부 클래스로 변경
    private class ServerRunnable implements Runnable {
        @Override
        public void run() {
            new Server().start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame();
            }
        });
    }
}


