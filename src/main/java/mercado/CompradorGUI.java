package mercado;

import jade.core.Agent;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompradorGUI  extends GuiAgent{
  private JTextField ingreseSuBúsquedaTextField;
  private JButton button1;
  private JPanel panel1;
  private Comprador agente;

  public CompradorGUI(Comprador a) {
    this.agente = a;
    button1 = new JButton();
    panel1 = new JPanel();

    // Ya debería de ser visible!!
    panel1.setVisible(true);
    button1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        agente.buscarProducto(ingreseSuBúsquedaTextField.getText(),1);
      }
    });
  }

  private void createUIComponents() {
    // TODO: place custom component creation code here
  }

  @Override
  protected void onGuiEvent(GuiEvent guiEvent) {

  }
}
