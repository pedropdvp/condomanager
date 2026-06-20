// URL base da API.
//  - Em localhost  -> "" (mesma origem): o backend local serve tambem o frontend.
//  - Publicado (ex.: GitHub Pages) -> URL do backend implantado (Railway).
// A detecao e automatica, pelo que o mesmo ficheiro funciona local e na cloud.
(function () {
    var host = location.hostname;
    var local = (host === 'localhost' || host === '127.0.0.1' || host === '');
    window.CONDO_API_BASE = local ? '' : 'https://condomanager-app-production.up.railway.app';
})();
