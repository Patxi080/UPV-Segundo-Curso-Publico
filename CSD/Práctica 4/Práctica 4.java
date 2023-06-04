//
// This file must be implemented when completing "ChatRobot activity"
//

import utils_rmi.ChatConfiguration;
import utils_rmi.RemoteUtils;
import faces.IChatChannel;
import faces.IChatMessage;
import faces.IChatServer;
import faces.IChatUser;
import faces.INameServer;
import faces.MessageListener;
import impl.ChatChannelImpl;
import impl.ChatMessageImpl;
import impl.ChatServerImpl;
import impl.ChatUserImpl;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

/**
 * ChatRobot implementation
 * 
 * <p> Notice ChatRobot implements MessageListener. MUST not extend ChatClient.
 * 
 */

public class ChatRobot implements MessageListener
{

    private ChatConfiguration conf;
    public ChatRobot (ChatConfiguration conf) {
        this.conf = conf;
        
       }
    
   @Override
   public void messageArrived (IChatMessage msg) {
       //*****************************************************************
       // Activity: implement robot message processing
       boolean isAlta = false;
       boolean isPrivado = false;
       try {
            //Mensaje normal a un canal o a un usuario
            if (msg != null && msg.getSender() != null && msg.getDestination() != null && msg.getText() != null) { 
                IChatUser sender = msg.getSender();
                String message = msg.getText();
                Remote destination = msg.getDestination();
                String visibilidad = "-";
                
                //No he encontrado forma de saber si procede del servidor
                if (msg.isPrivate()) {
                     visibilidad = "privado";
                     isPrivado = true;
                } else {
                    visibilidad = "publico";
                }
                System.out.println("Emisor: " + sender.getNick());
                System.out.println("Destino: " + RemoteUtils.remote2String(destination));
                System.out.println("Mensaje: " + message);
                System.out.println("Visibilidad: " + visibilidad);
            }else{
                if (msg.getSender() == null &&  msg.getDestination() != null && msg.getText() != null) {
                    //Menasje del servidor (sender == null)
                    String message = msg.getText();
                    Remote destination = msg.getDestination();
                    System.out.println("Emisor: servidor");
                    System.out.println("Destino: " + RemoteUtils.remote2String(destination));
                    System.out.println("Mensaje: " + message);
                    if(msg.getText().contains("JOIN")){
                        isAlta = true;
                    } 
                    
                    
                }
            } 
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }             
        
        //Por algún motivo falla al enviar el mensaje
        //Solucionar para completar el ejercicio
        /*
        if(isAlta){
            try {
                String[] args = msg.getText().split(" "); 
                
                String stringMensaje = "Hola " + args[1] ;
                
                Remote msgDest = (IChatChannel) msg.getDestination();
                
                ChatMessageImpl mensaje = new ChatMessageImpl(msg.getSender(), (IChatChannel) msgDest, stringMensaje);
                
                ((IChatChannel) msgDest).sendMessage((IChatMessage)mensaje);
                
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
          // msgD.sendMessage(mensaje);
        }
        if (isPrivado) {
            try {
                String stringMensaje = "Soy un robot, y la única respuesta que he aprendido hasta ahora es que 1 + 1 = 2.";
                IChatUser msgDest = (IChatUser) msg.getDestination();
                ChatMessageImpl mensaje = new ChatMessageImpl(msg.getSender(), msgDest, stringMensaje);
                msgDest.sendMessage(mensaje);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
        System.out.println();
        System.out.println("-------------------------------------------------------------------");
        System.out.println();
       
        
   }
   
   
   private void work () {
       
       String channelName = conf.getChannelName();
       if (channelName == null) channelName = "#Linux";
       System.out.println ("Robot will connect to server: '" + conf.getServerName() + "'" + 
               ", channel: '" + channelName + "'" + 
               ", nick: '" + conf.getNick() + "'" +        
               ", using name server: '" + conf.getNameServerHost() + ":" + conf.getNameServerPort() + "'");
       
       try {
           //*****************************************************************
           // Activity: implement robot connection and joining to channel
           //Encontrar el nombre del servidor de nombres Ejercicio 5.1
           //Registry reg = LocateRegistry.getRegistry (conf.getNameServerHost(), conf.getNameServerPort() ); 
           System.out.println();
           System.out.println("-------------------------------------------------------------------");
           System.out.println();
           INameServer reg = INameServer.getNameServer(conf.getNameServerHost(), conf.getNameServerPort());
           //System.out.println(reg);
           System.out.println(RemoteUtils.remote2String(reg));

           //Ejercicio 5.2
           String serverName = conf.getServerName();
           System.out.println("El nombre del servidor de chat es: " + serverName);
           
           IChatServer cs = (IChatServer) reg.lookup(serverName);
           
           System.out.println("La referencia remota es: " + RemoteUtils.remote2String(cs));

           
           //Ejercicio 5.3
           ChatUserImpl user = new ChatUserImpl(conf.getNick(), this);
           cs.connectUser(user);
           System.out.println("Conexión realizada con éxito.");
           
           System.out.println();
           System.out.println("-------------------------------------------------------------------");
           System.out.println();
           //Ejercicio 5.4
           IChatChannel[] canales = cs.listChannels();
           System.out.println("Los canales son:");
           for (IChatChannel channel : canales) {
                System.out.println(channel.getName());
           }
           System.out.println("Canal al que conectarse: " + conf.getChannelName());
           for (IChatChannel canal : canales) {
                if (canal.getName().equals(conf.getChannelName())) {
                    System.out.println("Bot conectado al canal: " + canal.getName());
                }
           }
           //Ejercicio 5.5
           
           IChatChannel channel = cs.getChannel(conf.getChannelName());
           
           IChatUser[] userList = channel.join(user);
           System.out.println(userList);
           System.out.println("Hola");
           System.out.println();
           System.out.println("-------------------------------------------------------------------");
           System.out.println();
           System.out.println("Tamaño lista usuarios: " + userList.length);
           System.out.println("Usuarios presentes en el chat: ");
           for (IChatUser iChatUser : userList) {
               // System.out.println("-------------------------------------------------------------------");
                System.out.println();
                System.out.println("Nombre: " + iChatUser.getNick());
                System.out.println("Referencia: " + RemoteUtils.remote2String(iChatUser));
                System.out.println();
                //System.out.println("-------------------------------------------------------------------");
           }
           System.out.println("-------------------------------------------------------------------");
           System.out.println();
           //Ejercicio 5.6
           String stringMensaje = "Hello World. Mi número favorito es el 42.";
           ChatMessageImpl mensaje = new ChatMessageImpl(user,channel, stringMensaje);
           channel.sendMessage(mensaje);
           System.out.println("Mensaje público enviado: " + stringMensaje);
           System.out.println();
           System.out.println("-------------------------------------------------------------------");
           System.out.println();

           //Ejercicio 5.7
           String stringMensajePrivado = "Hola, soy un bot.";
    
           IChatUser userPriv = null;
           for (IChatUser iChatUser : userList) {
                if (!iChatUser.equals(user)) {
                    userPriv = iChatUser;
                    ChatMessageImpl mensajePrivado = new ChatMessageImpl(user, userPriv, stringMensajePrivado);
                    userPriv.sendMessage(mensajePrivado);
                    break;
                }
           }
           System.out.println("Mensaje privado enviado: " + stringMensajePrivado);
           System.out.println("Mensaje privado enviado: " + userPriv.getNick());
           System.out.println();
           System.out.println("-------------------------------------------------------------------");
           System.out.println();
           
           

       } catch(java.rmi.ConnectException e){
           System.out.println(e.getMessage());
       }catch(java.rmi.NotBoundException e){
            System.out.println(e.getMessage());
       }catch (Exception e) {
           System.out.println(e.getMessage());
       }

   }

   public static void main (String args [])  {
       ChatRobot cr = new ChatRobot (ChatConfiguration.parse (args));
       
       cr.work ();
   }
}