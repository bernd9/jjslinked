function Product() {}

function ProductService(remoteService) {
    this.remoteService = remoteService;
}

ProductService.prototype.getProductList = function(category) {
    return this.remoteService.call('getProductList', {category: category});
}

ProductService.prototype.onNewOffer = function(product) { return ''};