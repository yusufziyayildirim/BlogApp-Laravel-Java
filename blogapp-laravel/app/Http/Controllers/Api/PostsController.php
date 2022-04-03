<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Post;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Storage;

class PostsController extends Controller
{
    public function create(Request $request){

        $post = new Post;
        $post->user_id = Auth::user()->id;
        $post->desc = $request->desc;

        //gönderide fotoğraf olup olmadığını kontrol etme
        if($request->photo != ''){
            //fotoğraf için benzersiz bir ad seçme
            $photo = time().'.jpg';
            file_put_contents('storage/posts/'.$photo,base64_decode($request->photo));
            $post->photo = $photo;
        }
        //mistake
        $post->save();
        $post->user;
        return response()->json([
            'success' => true,
            'message' => 'posted',
            'post' => $post 
        ]);
    }


    public function update(Request $request){
        $post = Post::find($request->id);
        // kullanıcının kendi gönderisini düzenleyip düzenlemediğini kontrol etme
        // Kullanıcı id ve postun kullanıcı idsini kontrol etme
        if(Auth::user()->id != $post->user_id){
            return response()->json([
                'success' => false,
                'message' => 'unauthorized access'
            ]);
        }
        $post->desc = $request->desc;
        $post->update();
        return response()->json([
            'success' => true,
            'message' => 'post edited'
        ]);
    }

    public function delete(Request $request){
        $post = Post::find($request->id);
        // kullanıcının kendi gönderisini düzenleyip düzenlemediğini kontrol etme
        if(Auth::user()->id !=$post->user_id){
            return response()->json([
                'success' => false,
                'message' => 'unauthorized access'
            ]);
        }
        
        //gönderinin silinecek fotoğrafı olup olmadığını kontrol etme
        if($post->photo != ''){
            Storage::delete('public/posts/'.$post->photo);
        }
        $post->delete();
        return response()->json([
            'success' => true,
            'message' => 'post deleted'
        ]);
    }

    public function posts(){
        $posts = Post::orderBy('id','desc')->get();
        foreach($posts as $post){
            //gönderinin kullanıcısını al
            $post->user;
            //yorum sayısı
            $post['commentsCount'] = count($post->comments);
            //beğeni sayısı
            $post['likesCount'] = count($post->likes);
            //kullanıcıların kendi gönderisini beğenip beğenmediğini kontrol etme
            $post['selfLike'] = false;
            foreach($post->likes as $like){
                if($like->user_id == Auth::user()->id){
                    $post['selfLike'] = true;
                }
            }

        }

        return response()->json([
            'success' => true,
            'posts' => $posts
        ]);
    }

    

    public function myPosts(){
        $posts = Post::where('user_id',Auth::user()->id)->orderBy('id','desc')->get();
        $user = Auth::user();
        return response()->json([
            'success' => true,
            'posts' => $posts,
            'user' => $user
        ]);
    }


}

