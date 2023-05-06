package com.tretornesp.participa.model;

import com.google.gson.Gson;

import java.util.List;

public class ProposalModel {
    private String author;
    private CoordinatesModel coordinates;
    private float created_at;
    private String description;
    private String id;
    private float likes;
    private String main_photo;
    private List<String> photos;
    private String title;

    private ProposalModel(String author, CoordinatesModel coordinates, float created_at, String description, String id, float likes, String main_photo, List<String> photos, String title) {
        this.author = author;
        this.coordinates = coordinates;
        this.created_at = created_at;
        this.description = description;
        this.id = id;
        this.likes = likes;
        this.main_photo = main_photo;
        this.photos = photos;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public CoordinatesModel getCoordinates() {
        return coordinates;
    }

    public float getCreated_at() {
        return created_at;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public float getLikes() {
        return likes;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public String getTitle() {
        return title;
    }

    public void setLikes(float likes) {
        this.likes = likes;
    }

    public static ProposalModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ProposalModel.class);
    }

    public void setMain_photo(String main_photo) {
        this.main_photo = main_photo;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public void setPhoto(String photo, String new_photo) {
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i) != null && photos.get(i).equals(photo)) {
                photos.set(i, new_photo);
                break;
            }
        }
    }

    /*
    public MaterialCardView toMaterialCardView(Context context) {
        MaterialCardView card = new MaterialCardView(context);
        card.setLayoutParams(new MaterialCardView.LayoutParams(
                MaterialCardView.LayoutParams.MATCH_PARENT,
                MaterialCardView.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView image = new ImageView(context);
        image.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                194
        ));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //Set default image
        image.setImageResource(R.drawable.ic_launcher_background);
        ImageRepository.putDrawable(main_photo, image);
        image.setContentDescription(title);

        LinearLayout secondLayout = new LinearLayout(context);
        secondLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        secondLayout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        title.setText(this.title);
        title.setTextAppearance(android.R.style.TextAppearance_Medium);

        TextView description = new TextView(context);
        description.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        description.setText(this.description);
        description.setTextAppearance(android.R.style.TextAppearance_Small);
        description.setTextColor(ContextCompat.getColor(context, android.R.color.secondary_text_light));

        ShapeableImageView fav = new ShapeableImageView(context);
        fav.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        fav.setContentDescription("Me gusta");
        fav.setClickable(true);
        fav.setFocusable(true);

        secondLayout.addView(title);
        secondLayout.addView(description);
        secondLayout.addView(fav);

        layout.addView(image);
        layout.addView(secondLayout);

        card.addView(layout);

        return card;
    }*/
}
