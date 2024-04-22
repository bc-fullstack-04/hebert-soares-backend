package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.NotFoundException;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;

import java.io.IOException;
import java.util.List;

@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@Tag(name = "Albums", description = "Albums API")

public class AlbumController {

    private final AlbumService albumService;
    @Operation(summary = "Get all albums from Spotify service by Text parameter")
    @GetMapping("/all")
    public ResponseEntity<List<AlbumModel>> getAllAlbums(@RequestParam("album") String album) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(album));
    }

    @Operation(summary = "Buy an album")
    @PostMapping("/sale")
    public ResponseEntity<Album> saveAlbum(@RequestBody Album album){
        Album purchasedAlbum = albumService.saveAlbum(album);
        return ResponseEntity.ok(purchasedAlbum);
    }
    @Operation(summary = "Get all albums from my collection")
    @GetMapping("/my-collection")
    public ResponseEntity<List<Album>> getCollection(){
        List<Album> myAlbums = albumService.myCollect();
        return ResponseEntity.ok(myAlbums);
    }

    @Operation(summary = "Remove an album by ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) throws UnauthorizedException, NotFoundException {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}




