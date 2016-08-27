import { APP_BASE_HREF } from '@angular/common';
import { disableDeprecatedForms, provideForms } from '@angular/forms';
import { enableProdMode } from '@angular/core';
import { PLATFORM_PIPES } from '@angular/core';
import { HTTP_PROVIDERS, Http } from '@angular/http';
import { TRANSLATE_PROVIDERS, TranslateService, TranslatePipe, TranslateLoader, TranslateStaticLoader } from 'ng2-translate/ng2-translate';
import { bootstrap } from '@angular/platform-browser-dynamic';

import {
  ErrorEventBus, ShopEventBus, I18nEventBus,
  ShopService,
  ShippingService, LocationService,
  OrganisationService,
  CatalogService,
  FulfilmentService,
  ManagementService,
  PaymentService,
  AttributeService,
  SystemService
} from './shared/services/index';

import {
  YcDatePipe, YcDateTimePipe
} from './shared/pipes/index'

import { APP_ROUTER_PROVIDERS } from './app.routes';
import { AppComponent } from './app.component';

if ('<%= ENV %>' === 'prod') { enableProdMode(); }

/**
 * Bootstraps the application and makes the ROUTER_PROVIDERS and the APP_BASE_HREF available to it.
 * @see https://angular.io/docs/ts/latest/api/platform-browser-dynamic/index/bootstrap-function.html
 */
bootstrap(AppComponent, [
  disableDeprecatedForms(),
  provideForms(),
  APP_ROUTER_PROVIDERS,
  {
    provide: APP_BASE_HREF,
    useValue: '<%= APP_BASE %>'
  },

  HTTP_PROVIDERS,

  /*
   * App wide i18n
   *
   * Use: {{ 'MODAL_CONFIRM_TITLE' | translate }} or formatted {{ 'MODAL_CONFIRM_DELETE' | translate:{value: urlToDelete} }}
   */
  {
    provide: TranslateLoader,
    useFactory: (http: Http) => new TranslateStaticLoader(http, 'assets/i18n', '.json'),
    deps: [Http]
  },
  TranslateService,
  {
    provide: PLATFORM_PIPES,
    useValue: TranslatePipe,
    multi: true
  },

  /*
   * Customer formatter pipes.
   */
  {
    provide: PLATFORM_PIPES,
    useValue: [ YcDatePipe, YcDateTimePipe ],
    multi: true
  },

  /*
   * App wide services singletons
   * Do not put these as provider in components as this will cause creation of new instances
   */
  ErrorEventBus,
  ShopEventBus,
  I18nEventBus,
  ShopService,
  ShippingService,
  LocationService,
  OrganisationService,
  CatalogService,
  FulfilmentService,
  ManagementService,
  PaymentService,
  AttributeService,
  SystemService
]);

// In order to start the Service Worker located at "./worker.js"
// uncomment this line. More about Service Workers here
// https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API/Using_Service_Workers
//
// if ('serviceWorker' in navigator) {
//   (<any>navigator).serviceWorker.register('./worker.js').then((registration: any) =>
//       console.log('ServiceWorker registration successful with scope: ', registration.scope))
//     .catch((err: any) =>
//       console.log('ServiceWorker registration failed: ', err));
// }
