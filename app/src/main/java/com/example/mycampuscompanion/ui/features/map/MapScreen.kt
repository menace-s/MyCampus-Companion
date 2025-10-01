package com.example.mycampuscompanion.ui.features.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker // N'oublie pas cet import !

@Composable
fun MapScreen() {
    // Remplace ces valeurs par les coordonnées exactes que tu as trouvées !
    val esaticLibraryPoint = GeoPoint(5.290718, -3.998327)

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                // On centre la carte sur le point d'intérêt
                controller.setCenter(esaticLibraryPoint)
                controller.setZoom(18.0) // On zoome un peu plus pour bien voir

                // --- AJOUT DU MARQUEUR ---
                // 1. On crée un nouvel objet Marqueur
                val poiMarker = Marker(this)

                // 2. On définit sa position géographique
                poiMarker.position = esaticLibraryPoint

                // 3. On définit le point d'ancrage de l'icône (important !)
                //    Ceci assure que la pointe de l'icône est sur les coordonnées,
                //    et non son centre.
                poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                // 4. On lui donne un titre (qui s'affichera au clic)
                poiMarker.title = "Bibliothèque de l'ESATIC"

                // 5. On ajoute le marqueur à la carte (étape cruciale !)
                this.overlays.add(poiMarker)

                // 6. On rafraîchit la carte pour être sûr que le marqueur s'affiche
                this.invalidate()
            }
        }
    )
}