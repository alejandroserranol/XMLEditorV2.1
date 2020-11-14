/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.File;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author aleja
 */
public class DOM {

    Document doc;

    public int abrir_XML_DOM(File _fichero) {

        //doc representará el árbol DOM.
        doc = null;

        try {
            //Se crea un objeto DocumentBuilderFactory.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            //Atributos del documento.
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);

            //Se crea un objeto DocumentBuilder para cargar en él
            //la estructura de árbol DOM de un fichero seleccionado
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Interpreta este fichero XML, lo mapea y guarda en memoria y
            //da el apuntador a la raíz.
            doc = builder.parse(_fichero);
            //Ahorastá listo para ser recorrido.

            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public String recorrer_DOM_y_mostrar() {
        String salida = "";
        Node node;
        String datos_nodo[] = null;

        //Obtiene el primer nodo del DOM (primer hijo).
        Node raiz = doc.getFirstChild();

        //Compruebo si el error se produce cuando se crea el árbol o en el método modifica_DOM
        //System.out.println(raiz.getNodeType() + " nodo " + raiz.getNodeName());
        //Obtiene una lista de nodos con todos los nodos hijo del raiz
        NodeList nodeList = raiz.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);

            //Antes de cada libro hay un nodo de tipo texto.
            //el problema está en la creación del árbol
            System.out.println("iteración " + i + "\t" + node.getNodeType() + " nodo " + node.getNodeName() + "\n");

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //Es un nodo libro

                datos_nodo = procesarLibro(node);
                salida += "\r\n " + "Publicado en: " + datos_nodo[0];
                salida += "\r\n " + "El título es: " + datos_nodo[1];
                salida += "\r\n " + "El autor es: " + datos_nodo[2];
                salida += "\r\n -------------------------";
            }
        }

        return salida;

    }

    private String[] procesarLibro(Node _node) {
        String datos[] = new String[3];
        Node ntemp = null;
        int contador = 1;

        //Obtiene el valor del primer atributo del nodo
        datos[0] = _node.getAttributes().item(0).getNodeValue();

        NodeList nodos = _node.getChildNodes();

        for (int i = 0; i < nodos.getLength(); i++) {
            ntemp = nodos.item(i);

            if (ntemp.getNodeType() == Node.ELEMENT_NODE) {
                //Para acceder al texto con el título y autor 
                //accedo al nodo text hijo de ntemp y se saca su valor.
                datos[contador] = ntemp.getFirstChild().getNodeValue();
                contador++;
            }
        }

        return datos;
    }

    public int annadirDom(String _titulo, String _autor, String _anno) {

        try {
            //Elemento título con nodo de texto
            Node ntitulo = doc.createElement("Titulo");
            Node ntitulo_text = doc.createTextNode(_titulo);
            ntitulo.appendChild(ntitulo_text);

            //Elemento autor con nodo de texto
            Node nautor = doc.createElement("Autor");
            Node nautor_text = doc.createTextNode(_autor);
            nautor.appendChild(nautor_text);

            //Se crea un nodo de tipo elemento (<libro>)
            Node nlibro = doc.createElement("Libro");
            ((Element) nlibro).setAttribute("publicado_en", _anno);

            //Se añade a libro el nodo autor y título creados antes.
            nlibro.appendChild(ntitulo);
            nlibro.appendChild(nautor);

            //Se obtiene el primer nodo del documento y a él se le añade como
            //hijo al nodo libro que ya tiene colgando todos sus hijos y atributos creados antes.
            Node raiz = doc.getFirstChild();
            raiz.appendChild(nlibro);

            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public int guardar_DOM_como_fichero(File _fichero) {

        try {
            //Crea un fichero de salida
            File archivo_xml = new File(_fichero.getAbsolutePath());
            //Especifico el formato de salida.
            OutputFormat format = new OutputFormat();
            //Especifica que la salida esté indentada
            format.setIndenting(true);

            XMLSerializer serializer = new XMLSerializer(new FileOutputStream(archivo_xml), format);

            serializer.serialize(doc);

            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public int modificar_DOM(String[] _textoAModificar) {

        try {
            //nodo libros
            Node raiz = doc.getFirstChild();
            //todos los nodos libro en una lista
            NodeList nodelistLibro = raiz.getChildNodes();
            //Elementos del nodelist
            Node nodoLibro;

            int[] elementosCambiados = procesarTextoIntroducido(_textoAModificar);

            //pregunto si hay algún cambio (0 = cambio || -1 = sin cambio)
            if (elementosCambiados[0] == 0 || elementosCambiados[1] == 0 || elementosCambiados[2] == 0) {

                //Nodo Libro a reemplazar
                Node nodoAReemplazar = creaNodoAReemplazar(elementosCambiados, _textoAModificar);

                //recorre la lista de nodos libro
                for (int j = 0; j < nodelistLibro.getLength(); j++) {
                    //Cada uno de los libros (nodo libro)
                    nodoLibro = nodelistLibro.item(j);

                    if (nodoLibro.getNodeType() == Node.ELEMENT_NODE) {

                    }

                }

            }

            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private int[] procesarTextoIntroducido(String[] _textoAModificar) {

        int[] auxiliar = {-1, -1, -1};

        for (int i = 0; i < _textoAModificar.length; i += 2) {
            //Compruebo que la pareja de elementos (antiguo y nuevo) no estén vacíos
            if (_textoAModificar[i].length() > 0 && _textoAModificar[i + 1].length() > 0) {
                auxiliar[1] = 0;
            }

            if (_textoAModificar[i].length() > 0 && _textoAModificar[i + 1].length() > 0) {
                auxiliar[2] = 0;
            }

            if (_textoAModificar[i].length() > 0 && _textoAModificar[i + 1].length() > 0) {
                auxiliar[3] = 0;
            }
        }

        return auxiliar;
    }

    private Node creaNodoAReemplazar(int[] _elementosCambiados, String[] _textoAModificar) {

        String sannoAntiguo = getAnnoAntiguo(_textoAModificar[4]);

        Node nautor;
        Node nautor_text;
        String sautorAntiguo = getAutorAntiguo(_textoAModificar[2]);

        Node ntitulo;
        Node ntitulo_text;
        String stituloAntiguo = getTituloAntiguo(_textoAModificar[0]);

        Node nlibro = null;

        for (int i = _elementosCambiados.length - 1; i <= 0; i--) {

            switch (_elementosCambiados[i]) {
                //Entra si no están vacíos tanto el campo de texto antiguo como nuevo
                case 0:
                    //Si entra cuando i==2 se quiere cambiar publicado_en
                    if (i == 2) {

                        if (_elementosCambiados[1] == 0) {
                            //si se modifica también el Autor añado el nuevo
                            nautor = doc.createElement("Autor");
                            nautor_text = doc.createTextNode(_textoAModificar[3]);
                            nautor.appendChild(nautor_text);
                        } else {
                            //En caso contrario, añado el antiguo
                            nautor = doc.createElement("Autor");
                            nautor_text = doc.createTextNode(sautorAntiguo);
                            nautor.appendChild(nautor_text);
                        }

                        if (_elementosCambiados[0] == 0) {
                            //si se modifica también el Titulo añado el nuevo
                            ntitulo = doc.createElement("Titulo");
                            ntitulo_text = doc.createTextNode(_textoAModificar[1]);
                            ntitulo.appendChild(ntitulo_text);
                        } else {
                            //En caso contrario, añado el antiguo
                            ntitulo = doc.createElement("Titulo");
                            ntitulo_text = doc.createTextNode(stituloAntiguo);
                            ntitulo.appendChild(ntitulo_text);
                        }

                        nlibro = doc.createElement("Libro");
                        ((Element) nlibro).setAttribute("publicado_en", _textoAModificar[5]);

                        nlibro.appendChild(nautor);
                        nlibro.appendChild(ntitulo);
                        //Ahora tengo formado el elemento Libro a reemplazar
                        return nlibro;
                    }
                    if (i == 1) {
                        //No tengo que modificar el año de publicación
                        //Autor nuevo
                        //Titulo puede haber cambiado
                        //Nodo libro con año antiguo
                        nlibro = doc.createElement("Libro");
                        ((Element) nlibro).setAttribute("publicado_en", sannoAntiguo);

                        //Creo un nodo Autor con el autor nuevo
                        nautor = doc.createElement("Autor");
                        nautor_text = doc.createTextNode(_textoAModificar[3]);
                        nautor.appendChild(nautor_text);

                        //creo un nodo Título (puede quererse cambiar o no)
                        if (_elementosCambiados[0] == 0) {
                            //si se modifica también el Titulo añado el nuevo
                            ntitulo = doc.createElement("Titulo");
                            ntitulo_text = doc.createTextNode(_textoAModificar[1]);
                            ntitulo.appendChild(ntitulo_text);
                        } else {
                            //En caso contrario, añado el antiguo
                            ntitulo = doc.createElement("Titulo");
                            ntitulo_text = doc.createTextNode(stituloAntiguo);
                            ntitulo.appendChild(ntitulo_text);
                        }

                        nlibro.appendChild(nautor);
                        nlibro.appendChild(ntitulo);
                        //Ahora tengo formado el elemento Libro a reemplazar
                        return nlibro;
                    }
                    if (i == 0) {
                        //si no ha entrado en los anteriores, 
                        //solo tengo que cambiar el título

                        //Nodo libro con año antiguo
                        nlibro = doc.createElement("Libro");
                        ((Element) nlibro).setAttribute("publicado_en", sannoAntiguo);

                        //Creo un nodo Autor con el autor antiguo
                        nautor = doc.createElement("Autor");
                        nautor_text = doc.createTextNode(sautorAntiguo);
                        nautor.appendChild(nautor_text);

                        //Creo un nodo Titulo con el Titulo nuevo
                        ntitulo = doc.createElement("Titulo");
                        ntitulo_text = doc.createTextNode(_textoAModificar[1]);
                        ntitulo.appendChild(ntitulo_text);

                        nlibro.appendChild(nautor);
                        nlibro.appendChild(ntitulo);
                        //Ahora tengo formado el elemento Libro a reemplazar
                        return nlibro;
                    }
                default:
                    break;
            }
        }
        //Sé que siempre va a entrar en case 0, nunca llega aquí
        return nlibro;
    }

    private String getAnnoAntiguo(String _textoAModificar) {
        String salida = "";
        Node nodeLibro;
        String datos_nodo[] = null;

        //nodo libros
        Node raiz = doc.getFirstChild();
        //todos los nodos libro en una lista
        NodeList nodeListLibro = raiz.getChildNodes();

        for (int i = 0; i < nodeListLibro.getLength(); i++) {
            nodeLibro = nodeListLibro.item(i);

            if (nodeLibro.getNodeType() == Node.ELEMENT_NODE) {
                //Es un nodo libro

                datos_nodo = procesarLibro(nodeLibro);

                if (datos_nodo[0] == _textoAModificar) {
                    salida = datos_nodo[1];
                }
            }
        }

        return salida;
    }

    private String getAutorAntiguo(String _textoAModificar) {
        String salida = "";
        Node nodeLibro;
        String datos_nodo[] = null;

        //nodo libros
        Node raiz = doc.getFirstChild();
        //todos los nodos libro en una lista
        NodeList nodeListLibro = raiz.getChildNodes();

        for (int i = 0; i < nodeListLibro.getLength(); i++) {
            nodeLibro = nodeListLibro.item(i);

            if (nodeLibro.getNodeType() == Node.ELEMENT_NODE) {
                //Es un nodo libro

                datos_nodo = procesarLibro(nodeLibro);

                if (datos_nodo[2] == _textoAModificar) {
                    salida = datos_nodo[1];
                }
            }
        }

        return salida;
    }

    private String getTituloAntiguo(String _textoAModificar) {
        String salida = "";
        Node nodeLibro;
        String datos_nodo[] = null;

        //nodo libros
        Node raiz = doc.getFirstChild();
        //todos los nodos libro en una lista
        NodeList nodeListLibro = raiz.getChildNodes();

        for (int i = 0; i < nodeListLibro.getLength(); i++) {
            nodeLibro = nodeListLibro.item(i);

            if (nodeLibro.getNodeType() == Node.ELEMENT_NODE) {
                //Es un nodo libro

                datos_nodo = procesarLibro(nodeLibro);

                if (datos_nodo[1] == _textoAModificar) {
                    salida = datos_nodo[1];
                }
            }
        }

        return salida;
    }

}
