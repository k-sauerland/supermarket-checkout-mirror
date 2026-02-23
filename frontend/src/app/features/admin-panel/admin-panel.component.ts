import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/service/product.service';
import { OfferService } from '../../core/service/offer.service';
import { CreateOfferDto } from '../../core/models/create-offer.dto';
import { MatLabel } from '@angular/material/input';
import { ProductState } from '../../core/state/product.state';
import { OfferState } from '../../core/state/offer.state';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../../shared/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, MatLabel, MatDialogModule],
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent {
  protected productState = inject(ProductState);
  protected offerState = inject(OfferState);

  private productService = inject(ProductService);
  private offerService = inject(OfferService);
  private dialog = inject(MatDialog);

  protected showProductPopup = signal(false);
  protected showOfferPopup = signal(false);
  protected errorMessage = signal<string | null>(null);

  protected newProduct = { name: '', price: 0 };
  protected newOffer: CreateOfferDto = { productId: '', requiredQuantity: 1, offerPrice: 0 };

  selectProductForOffer(productId: string): void {
    this.newOffer.productId = productId;
    this.showOfferPopup.set(true);
  }

  saveProduct(): void {
    if (this.newProduct.name && this.newProduct.price > 0) {
      this.productService.createProduct(this.newProduct).subscribe({
        next: () => {
          this.showProductPopup.set(false);
          this.newProduct = { name: '', price: 0 };
          this.productState.refresh();
        },
      });
    }
  }

  deleteProduct(id: string): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: { title: 'Archive Product', message: 'Are you sure you want to archive this product? This will also archive all associated weekly offers.' },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.productService.deleteProduct(id).subscribe({
          next: () => {
            this.productState.refresh();
            this.offerState.refresh();
          },
          error: (err) => {
            if (err.status === 409) {
              this.errorMessage.set("Can't delete: Product is used in a weekly offer.");
              setTimeout(() => this.errorMessage.set(null), 5000);
            }
          }
        });
      }
    });
  }

  saveOffer(): void {
    if (this.newOffer.productId && this.newOffer.requiredQuantity > 0) {
      this.offerService.createOffer(this.newOffer).subscribe({
        next: () => {
          this.showOfferPopup.set(false);
          this.newOffer = { productId: '', requiredQuantity: 1, offerPrice: 0 };
          this.offerState.refresh();
        },
      });
    }
  }

  deleteOffer(id: string): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: { title: 'Archive Offer', message: 'Are you sure you want to archive this offer?' },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.offerService.deleteOffer(id).subscribe({
          next: () => this.offerState.refresh(),
        });
      }
    });
  }
}
