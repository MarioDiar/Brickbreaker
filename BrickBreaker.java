
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MarioDiaz
 */
public class BrickBreaker extends JFrame implements Runnable, KeyListener {
    
    //Objetos para manejar el buffer del applet y que no parpadee
    private Image imaImagenApplet;
    private Graphics graGraficaApplet;
    private int iVidas;//vidas
    private int iScore;//score
    private int iDireccionBarra;//direccion de la barra
    private LinkedList lnkBloques;//lista encaneda de bloques
    private Objeto objBloque;//objeto bloque
    private Objeto objBola;//objeto bola
    private int iVelBolaX;//Velocidad de la bola
    private int iVelBolaY;//Velocidad de la bola
    private int iPosBola;//posicion de la pelota en la barra(cuando choca)
    private int iAngulo;//angulo con el que pega la pelota en la barra
    private boolean bBolaMueve;//boolean para saber si se esta moviendo la bola
    private Objeto objBarra;//objeto barra
    private boolean bBarraMueve;//boolean para controlar la barra
    
    
    public BrickBreaker () {
        init();
        start();
    }
    
    public void init () {
        setSize (650, 750);
        
        iVidas = 3;
        
        //Inicializa la barra
        URL urlImagenBarra = this.getClass().getResource("barraprueba.png");

        objBarra = new Objeto(0,0,
                Toolkit.getDefaultToolkit().getImage(urlImagenBarra));
        objBarra.setX(getWidth()/2 - objBarra.getAncho()/2);
        objBarra.setY(getHeight() - (objBarra.getAlto() + 2));
        
        //Inicializa la direccion de la barra
        iDireccionBarra = 0;
        
        //Se inicializa la lista encadenada de bloques
        
        lnkBloques = new LinkedList();
        //Se carga la imagen del bloque en el URL
        URL urlImagenBloque = this.getClass().
                getResource("methblock.png");
        
        int posXtemp = 0;
        
        for (int iI = 1; iI <= 10; iI++) {
            objBloque = new Objeto(0,0,
                Toolkit.getDefaultToolkit().getImage(urlImagenBloque));

                int posY = getHeight()/4;
                int posX = posXtemp;
                posXtemp += objBloque.getAncho() + 3;

            objBloque.setX(posX);
            objBloque.setY(posY);
            lnkBloques.add(objBloque);
        }
        
        //Se inicializa la bola
        URL urlImagenBola = this.getClass().
                getResource("pruebaBola.png");
        
        objBola = new Objeto(0,0,
                Toolkit.getDefaultToolkit().getImage(urlImagenBola));
        objBola.setX(getWidth()/2 - objBola.getAncho()/2);
        objBola.setY(getHeight() - (objBarra.getAlto() +
                objBola.getAlto() * 10));
        
        //Direccion de la bola
        iVelBolaX = 2;
        iVelBolaY = 2;
        
        bBolaMueve = true;
      
        //Se anade el keylistener
        addKeyListener(this);
    }
    
    public void start () {
        //Declaras un hilo
        Thread th = new Thread (this);
        //Empieza el hilo
        th.start();
    }
    
    public void run () {
        //Se hace el ciclo del juego mientras se tengan vidas
        while (iVidas > 0) {
            actualiza();
            checaColision();
            repaint();
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError)	{
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
        }
    }
    
    public void actualiza(){
        //Actualiza de la barra
        if (bBarraMueve){
            if (iDireccionBarra == 3) {
                objBarra.setX(objBarra.getX() + 10);
            }
            if (iDireccionBarra == 4) {
                objBarra.setX(objBarra.getX() - 10);
            } 
        }
        //Actualiza la bola
        
        if (bBolaMueve) {
            objBola.setX(objBola.getX() + iVelBolaX);
            objBola.setY(objBola.getY() + iVelBolaY);
        }
    }
    
    public void checaColision () {
        //Checa colision de la barra con las paredes
        if (objBarra.getX() < 0) {
            objBarra.setX(0);
        }
        if (objBarra.getX() + objBarra.getAncho() > getWidth()) {
            objBarra.setX(getWidth() - objBarra.getAncho());
        }
        
        //Checa colision de la BOLA con el demas environment
        //Checa colision de la bola con la pared de la izquierda
        if (objBola.getX() < 3) {
            iVelBolaX *= -1;
        }
        //Checa la colision con la pared de arriba
        if (objBola.getY() < 25) {
            iVelBolaY *= -1;
        } 
        //Checa la colision con la pared de la derecha
        if (objBola.getX() + objBola.getAncho() + 3 > getWidth()) {
            iVelBolaX *= -1;
        }
        if (objBola.getY() + objBola.getAlto() + 3 > getHeight()) {
            iVelBolaY *= -1;
        }
        //Checa colision de la bola con la barra
        if (objBola.colisiona(objBarra)) {
            calculaAngulo();
        }
        //Checa colision de la bola con los bloques
        for (Iterator<Objeto> iter = lnkBloques.iterator(); iter.hasNext();){
            Objeto objBloqueTemp = iter.next();
            if (objBola.colisiona(objBloqueTemp)) {
                iter.remove();
                iVelBolaY *= -1;
            }
        }
    }
    
    public void calculaAngulo() {
        iPosBola = objBola.getX() - objBola.getY();
        
        iAngulo = (iPosBola / (objBarra.getAncho() - objBola.getAncho()))
                    - (1/2);
        
        iVelBolaX = iAngulo * 3;
        iVelBolaY *= -1;
    }
    
    public void paint (Graphics graGrafico) {
        //Se inicializa el doublebuffer
        if (imaImagenApplet == null) {
            imaImagenApplet = createImage (this.getSize().width,
                    this.getSize().height);
            graGraficaApplet = imaImagenApplet.getGraphics();
        }
        //Crea la imagen para el brackground
        URL urlImagenFondo = this.getClass().getResource("starsbackground.jpg");
        Image imaImagenEspacio = Toolkit.getDefaultToolkit().
                getImage(urlImagenFondo);
        //Despliega la imagen de fondo
        graGraficaApplet.drawImage(imaImagenEspacio, 0, 0,
                getWidth(), getHeight(), this);
        //Actualiza el foreground
        graGraficaApplet.setColor (getForeground());
        paint1(graGraficaApplet);
        //Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }
    
    public void paint1 (Graphics g) {
        if (iVidas > 0) {
            //Se pinta la barra
            g.drawImage(objBarra.getImagen(), objBarra.getX(),
                    objBarra.getY(), this);
            //Se pinta la bola
            g.drawImage(objBola.getImagen(), objBola.getX(),
                    objBola.getY(), this);
            //Se pintan los bloques
            for (Object lnkBloque : lnkBloques) {
                Objeto objBloque = (Objeto) lnkBloque;
                g.drawImage(objBloque.getImagen(),
                        objBloque.getX(), objBloque.getY(), this);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            bBarraMueve = true;
            iDireccionBarra = 3;
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            bBarraMueve = true;
            iDireccionBarra = 4;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            bBarraMueve = false;
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            bBarraMueve = false;
        }
    }
}
