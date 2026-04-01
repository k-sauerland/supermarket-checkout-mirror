import { Component } from '@angular/core';
import { CommonModule} from '@angular/common';
import {ProductListComponent} from '../product-list/product-list.component';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [
    CommonModule,
    ProductListComponent
  ],
  templateUrl: './shop.html',
  styleUrl: './shop.scss',
})
export class Shop {

}
