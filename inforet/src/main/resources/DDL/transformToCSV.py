import pandas as pd

df = pd.read_csv('spotify_millsongdata.csv')

new_df = pd.DataFrame({
    'Title': df.iloc[:, 0],
    'Artist': df.iloc[:, 1],
    'Lyrics': df.iloc[:, 3]
})

new_df.to_csv('songs.csv', index=False)
