import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Práctica 5.4 - Catálogo de libros en XML y leer del fichero.
 * En ésta práctica crearemos una clase llamada CatalogoLibrosXML para almacenar
 * un conjunto de libros leídos desde un fichero XML.
 */
enum class OS() {
    WINDOWS, LINUX
}

fun getOS(): OS? {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> {
            OS.WINDOWS
        }
        os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
            OS.LINUX
        }
        else -> null
    }
}


//Función para leer el archivo XML
class CatalogoLibrosXML(cargador: String) {
    val fichero: Document? = readXml(cargador)
    var listadoNodos: MutableList<Node> = mutableListOf()

    init {
        if (fichero == null) {
            throw IllegalArgumentException("El fichero seleccionado no existe.")
        } else {
            listadoNodos = obtenerListaNodosPorNombre(fichero,"book")
        }
    }

    fun existeLibro(idLibro: String): Boolean {
        /**
         * Función para comprobar si el libro existe.
         */
        var libroExiste: Boolean = false
            listadoNodos.forEach {
                if(it.firstChild.nextSibling.firstChild.nodeValue.lowercase() == idLibro.lowercase()) {
                    libroExiste = true
                }
            }
        return libroExiste
    }

    fun infoLibro(idLibro: String): MutableMap<String,Any> {
        /**
         * Función para  obtener la información del libro
         */
        return if (existeLibro(idLibro)) {
            obtenerMapInfoLibros(obtenerNodoPorIdLibro(idLibro) as Element)
        } else {
            mutableMapOf<String,Any>()
        }
    }

    /**
     * Funciones auxiliares
     */
    private fun readXml(pathName:String): Document
    {
        /**
         * Función para leer el fichero XML que devuelve un Document.
         */
        val xmlFile = File(pathName)
        return  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
    }
    private fun obtenerNodoPorIdLibro(idLibro: String): Node {
        /**
         * Funcion para obtener el nodo que utilizaremos en infoLibro
         */
        var indexNode = 0
        var contador = 0
        listadoNodos.forEach {
            if(it.firstChild.nextSibling.firstChild.nodeValue.lowercase() == idLibro.lowercase()) {
                indexNode = contador
            }
            contador++
        }
        return listadoNodos[indexNode]
    }


    //Función para obtener un listado de nodos.
    private fun obtenerListaNodosPorNombre(doc: Document, tagName: String): MutableList<Node>
    {
        /**
         * Función con la que obtenemos un listado de los nodos existentes en el fichero.
         */
        val bookList: NodeList = doc.getElementsByTagName(tagName)
        val lista = mutableListOf<Node>()
        for (i in 0..bookList.length - 1)
            lista.add(bookList.item(i))
        return lista
    }

    private fun obtenerMapInfoLibros(e: Element): MutableMap<String,Any> {
        /**
         * Funcion para obtener el mapa que devuelve infoLibro
         */
        val listaNodos: MutableMap<String,Any> = mutableMapOf()

        val listaAutores = e.getElementsByTagName("author")
        val dimensiones: MutableMap<String,String> = obtenerAtributosEnMapKV(e.getElementsByTagName("dimensions").item(0) as Element)

        listaNodos["title"] = e.getElementsByTagName("title").item(0).textContent
        listaNodos["authors"] = obtenerAutor(listaAutores)
        listaNodos["isbn"] = e.getElementsByTagName("isbn").item(0).textContent
        listaNodos["Dimensions"] = dimensiones

        return listaNodos

    }

    private fun obtenerAutor(listaAutores: NodeList): MutableMap<String, String> {
        /**
         * Funcion para obtener los autores.
         * Al saber que es posible que existan dos autores, he creado una lista con los nombres de los autores.
         * Aparecera un unico nombre si solo existe un autor.
         *
         * PD: Por motivos que desconozco, en autores no me funciona el método que he utilizado en el resto de nodos del libro.
         */
        var autores: MutableMap<String,String> = mutableMapOf()
        for (i in 0 until listaAutores.length - 1) {
            autores.put(listaAutores.item(i).nodeName,listaAutores.item(i).textContent)
        }
        return autores
    }


    private fun obtenerAtributosEnMapKV(e: Element):MutableMap<String, String>
    {
        /**
         * Función que obtiene los atributos de un nodo en específico.
         */
        val mMap = mutableMapOf<String, String>()
        for(j in 0..e.attributes.length - 1)
            mMap.putIfAbsent(e.attributes.item(j).nodeName, e.attributes.item(j).nodeValue)
        return mMap
    }

}
fun main() {
    var os = getOS()
    val rute: String = if (os == OS.WINDOWS) {
                            "XML\\items.xml"
                        } else {
                            "XML/items.xml"
                        }

    val fichero = CatalogoLibrosXML(rute)

    println(fichero.infoLibro("la colmena"))

}
