package br.com.sysmap.bootcamp.domain.service.integration;

import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.mapper.AlbumMapper;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SpotifyAPI {

    private static final String CLIENT_ID = "96b2342e09f94783983503cfe34ea124";
    private static final String CLIENT_SECRET = "1a56d012077d4c8482a65e6167e60b79";

    private SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .build();

    public List<AlbumModel> getAlbums(@RequestParam("Album") String albumName) throws IOException, ParseException, SpotifyWebApiException {
        spotifyApi.setAccessToken(fetchAccessToken());
        Random priceRandomizer = new Random();
        DecimalFormat priceFormatter = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.ENGLISH));

        return AlbumMapper.album_mapper.toModel(
                        spotifyApi.searchAlbums(albumName)
                                .market(CountryCode.BR)
                                .limit(25)
                                .build()
                                .execute()
                                .getItems())
                .stream()
                .map(album -> applyRandomPriceToAlbum(album, priceRandomizer, priceFormatter))
                .collect(Collectors.toList());
    }

    private AlbumModel applyRandomPriceToAlbum(AlbumModel album, Random priceRandomizer, DecimalFormat priceFormatter) {
        double randomPrice = priceRandomizer.nextDouble() * 100;
        album.setAlbumValue(new BigDecimal(priceFormatter.format(randomPrice)));
        return album;
    }

    private String fetchAccessToken() throws IOException, ParseException, SpotifyWebApiException {
        ClientCredentialsRequest credentialsRequest = spotifyApi.clientCredentials().build();
        return credentialsRequest.execute().getAccessToken();
    }
}
