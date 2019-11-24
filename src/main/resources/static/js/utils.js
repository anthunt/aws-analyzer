(function(global, factory) {
	(global = global || self, global.Utils = factory());
}(this, function() { 'use strict';
	return new function() {
		var _this = this;
		
		_this.notify = (type, message) => {
	    	$.notify({
	    		message: message 
	    	},{
	    		type: type
	    	    , delay: 2000
	    	    , placement: {
	    			from: 'top',
	    			align: 'right'
	    	    }
	    	});
	    };
	    
	    _this.async = (url = '', processing = ()=>{}, complete = ()=>{}) => {
	    	_this.post(url, null, processing, complete);
	    };
	    
		_this.post = (url = '', data = {}, processing = ()=>{}, complete = ()=>{}) => {
			
			fetch(url, {
				method: data == null ? 'GET' : 'POST',
				mode: 'no-cors',
				cache: 'no-cache',
				credentials: 'same-origin',
				headers: {
		            'Content-Type': 'application/json'
		        },
		        redirect: 'follow',
		        referrer: 'no-referrer',
		        body: data == null ? null : JSON.stringify(data)
			})
			.catch(error => {
				_this.notify('danger', error);
				if(typeof complete === 'function') complete();
			})
			.then(res => {
				if(!res.ok) {
					_this.notify('danger', res.error);
				}
				
				if(res.redirected) {
					location.href = res.url;
					return;
				}
				
				return res.json(); 
			})
			.catch(error => {
				_this.notify('danger', error);
				if(typeof complete === 'function') complete();
			})
			.then(res => {
				if(res.success) {
					if(typeof processing === 'function') processing(res.data);
				} else {
					if(res.error.redirect) {
						location.href = res.error.redirect;
						return;
					} else {
						_this.notify('danger', res.error.message);
					}
				}
				if(typeof complete === 'function') complete();
			})
			.catch(error => {
				_this.notify('danger', error);
				if(typeof complete === 'function') complete();
			});
			
        };
        
    }();
}));